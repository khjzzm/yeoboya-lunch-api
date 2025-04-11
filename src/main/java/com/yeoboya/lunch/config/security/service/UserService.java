package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.board.base.repository.like.LikeRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.reply.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardRepository;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.common.service.EmailService;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.file.repository.MemberProfileFileRepository;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.domain.LoginInfo;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.repository.LoginInfoRepository;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.config.annotation.Retry;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.UserSecurityStatus;
import com.yeoboya.lunch.config.security.domain.WithdrawnMember;
import com.yeoboya.lunch.config.security.dto.Token;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.repository.WithdrawnMemberRepository;
import com.yeoboya.lunch.config.security.reqeust.UserRequest;
import com.yeoboya.lunch.config.security.reqeust.UserRequest.*;
import com.yeoboya.lunch.config.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    // Repository related fields
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final LoginInfoRepository loginInfoRepository;
    private final WithdrawnMemberRepository withdrawnMemberRepository;

    private final ReplyRepository replyRepository;
    private final LikeRepository likeRepository;
    private final FreeBoardRepository freeBoardRepository;

    // Service fields
    private final EmailService emailService;
    private final FileServiceS3 fileServiceS3;
    private final MemberProfileFileRepository memberProfileFileRepository;

    // Utility and Security fields used for the project
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Other fields
    private final Response response;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${front.url}")
    private String frontUrl;

    @Retry(value = 4)
    public ResponseEntity<Body> signUp(SignUp signUp) {
        // create member
        Member build = Member.builder()
                .loginId(signUp.getLoginId())
                .email(signUp.getEmail())
                .name(signUp.getName())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .provider(signUp.getProvider())
                .build();
        Role role;
        if (build.getLoginId().equals("admin")) {
            role = roleRepository.findByRole(Authority.ROLE_ADMIN);
        } else {
            role = roleRepository.findByRole(Authority.ROLE_USER);
        }

        // set member_info
        MemberInfo memberInfo = MemberInfo.createMemberInfo(signUp, build);

        // set UserSecurityStatus
        UserSecurityStatus userSecurityStatus = UserSecurityStatus.createUserSecurityStatus(build);

        // save member
        Member saveMember = Member.createMember(build, memberInfo, role, userSecurityStatus);
        Member save = memberRepository.save(saveMember);

        return response.success(Code.SAVE_SUCCESS, save.getId());
    }

    public ResponseEntity<Body> socialSignUp(@Valid UserRequest.SocialSignUp socialSignUp, HttpServletResponse httpServletResponse) {
        Member member = memberRepository.findByLoginIdAndProvider(socialSignUp.getLoginId(), socialSignUp.getProvider())
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + socialSignUp.getLoginId()));

        // 회원정보 설정(update)
        member.setEmail(socialSignUp.getEmail());
        MemberInfo memberInfo = MemberInfo.createMemberInfo(socialSignUp, member);
        UserSecurityStatus userSecurityStatus = UserSecurityStatus.createUserSecurityStatus(member);

        member.setRole(roleRepository.findByRole(Authority.ROLE_USER));
        member.addMemberInfo(memberInfo);
        member.addUserSecurityStatus(userSecurityStatus);
        Member save = memberRepository.save(member);

        // 회원이미지 설정
        Function<FileResponse, ProfileResponse> responseMapper = ProfileResponse::apply;
        ProfileResponse upload = fileServiceS3.upload(socialSignUp.getProfileImageUrl(), Directory.PROFILE, responseMapper);

        boolean isDefault = memberRepository.profileImg(member.getLoginId()).stream()
                .anyMatch(MemberProfileFile::getIsDefault);
        upload.setIsDefault(!isDefault);

        MemberProfileFile profileFileEntity = MemberProfileFile.builder()
                .member(member)
                .profileResponse(upload)
                .build();

        MemberProfileFile memberProfileFile = memberProfileFileRepository.save(profileFileEntity);

        // 회원가입 후 토큰 발급
        Token token = jwtTokenProvider.generateToken(member.getLoginId(), List.of(new SimpleGrantedAuthority(member.getRole().getRole().toString())));
        CookieUtils.setAuthCookies(httpServletResponse, token, activeProfile);

        //  Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + member.getLoginId(),
                token.getRefreshToken(),
                token.getRefreshTokenExpirationTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS);

        return response.success(Code.SAVE_SUCCESS, token.getAccessToken());
    }

    public ResponseEntity<Body> signIn(SignIn signIn, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Optional<Member> matchedMember = memberRepository.findByLoginId(signIn.getLoginId());
        matchedMember.ifPresentOrElse(member -> {
            LoginInfo loginInfo = LoginInfo.buildLoginInfo(member, httpServletRequest);
            loginInfoRepository.save(loginInfo);
        }, () -> response.fail(ErrorCode.USER_NOT_FOUND));

        // loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(signIn.toAuthentication());

        // 토큰 발급
        Token token = jwtTokenProvider.generateToken(authentication);
        CookieUtils.setAuthCookies(httpServletResponse, token, activeProfile);

        // Redis 저장
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                token.getRefreshToken(),
                token.getRefreshTokenExpirationTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS);

        return response.success(Code.SEARCH_SUCCESS, token.getAccessToken()); // todo token은 쿠키로 내려가므로 body는 null or 최소 정보
    }


    public ResponseEntity<Body> signOut(SignOut signOut, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 1️⃣ accessToken 우선순위: body > Authorization > 쿠키
        String accessToken = Optional.ofNullable(signOut)
                .map(SignOut::getAccessToken)
                .orElse(jwtTokenProvider.resolveToken(httpServletRequest));

        if (!jwtTokenProvider.validateToken(accessToken)) {
            return response.fail(ErrorCode.INVALID_AUTH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // Redis에서 리프레시 토큰 제거
        String redisRT = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (!ObjectUtils.isEmpty(redisRT)) {
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // Redis 로그아웃 토큰 블랙리스트 처리
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set("LOT:" + accessToken,
                authentication.getName(),
                expiration,
                TimeUnit.MILLISECONDS);

        // 쿠키 제거
        CookieUtils.deleteAuthCookies(httpServletResponse, activeProfile);

        return response.success("로그아웃 되었습니다.");
    }


    public ResponseEntity<Body> reissue(String refreshToken, HttpServletResponse httpServletResponse) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return response.fail(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationWithLoadUserByUsername(refreshToken);

        String redisRT = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (ObjectUtils.isEmpty(redisRT) || !redisRT.equals(refreshToken)) {
            return response.fail(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Token token = jwtTokenProvider.generateToken(authentication);
        CookieUtils.setAuthCookies(httpServletResponse, token, activeProfile);

        // Redis 저장
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                token.getRefreshToken(),
                token.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS);
        return response.success(Code.UPDATE_SUCCESS, token.getAccessToken());
    }

    @Transactional
    public ResponseEntity<Body> changePassword(Credentials credentials) {
        Member member = memberRepository.findByLoginIdAndEmail(credentials.getLoginId(), credentials.getEmail()).
                orElseThrow(() -> new EntityNotFoundException("Member not found - " + credentials.getLoginId() + "/" + credentials.getEmail()));

        if (!passwordEncoder.matches(credentials.getOldPassword(), member.getPassword())) {
            return response.fail(ErrorCode.INVALID_OLD_PASSWORD);
        }

        if (!credentials.getNewPassword().equals(credentials.getConfirmNewPassword())) {
            return response.fail(ErrorCode.INVALID_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(credentials.getNewPassword()));
        memberRepository.save(member); // 명시적으로 저장

        return response.success(Code.UPDATE_SUCCESS);
    }


    @Transactional
    public ResponseEntity<Body> resetPassword(Credentials credentials) {
        Member member = memberRepository.findByLoginId(credentials.getLoginId()).
                orElseThrow(() -> new EntityNotFoundException("Member not found - " + credentials.getLoginId()));

        String key = "EMAIL:" + credentials.getEmail();
        String passKey = redisTemplate.opsForValue().get(key);

        if (ObjectUtils.isEmpty(passKey) || !passKey.equals(credentials.getPassKey())) {
            return response.fail(ErrorCode.INTERNAL_SERVER_ERROR_OCCURRED);
        }

        if (!credentials.getNewPassword().equals(credentials.getConfirmNewPassword())) {
            return response.fail(ErrorCode.INVALID_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(credentials.getNewPassword()));
        redisTemplate.delete(key);

        return response.success(Code.UPDATE_SUCCESS);
    }

    public ResponseEntity<Body> sendResetPasswordMail(ResetPassword resetPassword) {
        String loginId = resetPassword.getLoginId();
        String email = resetPassword.getEmail();
        boolean exists = !memberRepository.existsMemberByLoginIdAndEmail(loginId, email);

        if (exists) {
            String errorMessage = "입력하신 아이디를 찾을 수 없습니다.";
            throw new EntityNotFoundException(errorMessage);
        }

        String passKey = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("EMAIL:" + email, passKey, 3, TimeUnit.HOURS);

        String authorityPage = frontUrl + resetPassword.getAuthorityPage()
                + "?pass_key=" + passKey
                + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&login_id=" + URLEncoder.encode(loginId, StandardCharsets.UTF_8);

        emailService.resetPassword(email, authorityPage);
        return response.success("메일전송");
    }


    public ResponseEntity<Body> findLoginId(String email) {
        return memberRepository.findLoginIdByEmailAndProvider(email, "yeoboya")
                .map(this::maskLoginId)
                .map(maskedId -> response.success(Code.SEARCH_SUCCESS, maskedId))
                .orElseGet(() -> response.fail(ErrorCode.INVALID_USER, email));
    }


    @Transactional
    public ResponseEntity<Body> withdrawMember(WithdrawRequest withdrawRequest) {
        Member member = memberRepository.findByLoginIdAndProvider(withdrawRequest.getLoginId(), withdrawRequest.getProvider())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        WithdrawnMember withdrawn = WithdrawnMember.builder()
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .provider(member.getProvider())
                .reason(withdrawRequest.getReason())
                .withdrawnAt(LocalDateTime.now())
                .build();

        withdrawnMemberRepository.save(withdrawn);

        // Dummy Member 가져오기 (고정된 id 혹은 loginId로)
        Member dummy = Member.builder()
                .loginId("withdrawn-" + UUID.randomUUID())
                .email("withdrawn-" + System.currentTimeMillis() + "@dummy.com")
                .provider(withdrawn.getProvider())
                .name("탈퇴회원")
                .password(null)
                .role(roleRepository.findByRole(Authority.ROLE_BLOCK))
                .build();
        memberRepository.save(dummy);

        // 댓글/좋아요/게시글 등 연관된 엔티티의 member 변경 및 삭제
        replyRepository.updateMemberToDummy(member, dummy);
        likeRepository.updateMemberToDummy(member, dummy);
        freeBoardRepository.deleteAllByMember(member);

        // 관련 정보 삭제
        memberRepository.delete(member);

        return response.success(Code.DELETE_SUCCESS);
    }

    private String maskLoginId(String loginId) {
        if (loginId == null) return "";

        int length = loginId.length();
        if (length <= 2) {
            return loginId; // 그대로 반환
        } else if (length == 3) {
            return loginId.substring(0, 2) + "*";
        } else if (length == 4) {
            return loginId.charAt(0) + "**" + loginId.charAt(3);
        } else {
            return loginId.substring(0, 2) + "***" + loginId.charAt(length - 1);
        }
    }
}
