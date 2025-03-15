package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.common.service.EmailService;
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
import com.yeoboya.lunch.config.security.dto.Token;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.reqeust.UserRequest.*;
import com.yeoboya.lunch.config.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    // Repository related fields
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final LoginInfoRepository loginInfoRepository;

    // Service fields
    private final EmailService emailService;

    // Utility and Security fields used for the project
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Other fields
    private final Response response;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;

    @Retry(value = 4)
    public ResponseEntity<Body> signUp(SignUp signUp) {
        if (memberRepository.existsMemberByLoginId(signUp.getLoginId())) {
            return response.fail(ErrorCode.USER_DUPLICATE_ID);
        }

        if (memberRepository.existsByEmailAndProvider(signUp.getEmail(), signUp.getProvider())) {
            return response.fail(ErrorCode.USER_DUPLICATE_EMAIL);
        }

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
        MemberInfo memberInfo = MemberInfo.createMemberInfo(build);

        // set UserSecurityStatus
        UserSecurityStatus userSecurityStatus = UserSecurityStatus.createUserSecurityStatus(build);

        // save member
        Member saveMember = Member.createMember(build, memberInfo, role, userSecurityStatus);
        Member save = memberRepository.save(saveMember);

        return response.success(Code.SAVE_SUCCESS, save.getId());
    }

    public ResponseEntity<Body> signIn(SignIn signIn, HttpServletRequest httpServletRequest) {
        Optional<Member> matchedMember = memberRepository.findByLoginId(signIn.getLoginId());
        matchedMember.ifPresentOrElse(member -> {
            LoginInfo loginInfo = LoginInfo.buildLoginInfo(member, httpServletRequest);
            loginInfoRepository.save(loginInfo);
        }, () -> response.fail(ErrorCode.USER_NOT_FOUND));

        // loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(signIn.toAuthentication());
        Token token = jwtTokenProvider.generateToken(authentication, "yeoboya", signIn.getLoginId());

        // save redis refreshToken
        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                token.getRefreshToken(),
                token.getRefreshTokenExpirationTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS);

        return response.success(Code.SEARCH_SUCCESS, token);
    }

    public ResponseEntity<Body> signOut(SignOut signOut) {
        if (!jwtTokenProvider.validateToken(signOut.getAccessToken())) {
            return response.fail(ErrorCode.INVALID_AUTH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(signOut.getAccessToken());

        String redisRT = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (!ObjectUtils.isEmpty(redisRT)) {
            redisTemplate.delete("RT:" + authentication.getName());
        }

        //add redis blacklist
        Long expiration = jwtTokenProvider.getExpiration(signOut.getAccessToken());
        redisTemplate.opsForValue().set("LOT:" + signOut.getAccessToken(),
                "logout",
                expiration,
                TimeUnit.MILLISECONDS);

        return response.success("로그아웃 되었습니다.");
    }

    public ResponseEntity<Body> reissue(Reissue reissue) {
        Cookie cookie = CookieUtils.getCookie("RefreshToken");
        String refreshToken = reissue.getRefreshToken().isEmpty() ? Objects.requireNonNull(cookie).getValue() : reissue.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return response.fail(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationWithLoadUserByUsername(refreshToken);

        String redisRT = redisTemplate.opsForValue().get("RT:" + authentication.getName());

        if (ObjectUtils.isEmpty(redisRT)) {
            return response.fail(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!redisRT.equals(refreshToken)) {
            return response.fail(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Token token = jwtTokenProvider.generateToken(authentication, reissue.getProvider(), reissue.getLoginId());

        redisTemplate.opsForValue().set("RT:" + authentication.getName(),
                token.getRefreshToken(),
                token.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS);

        return response.success(Code.UPDATE_SUCCESS, token);
    }

    @Transactional
    public ResponseEntity<Body> changePassword(Credentials credentials) {
        Member member = memberRepository.findByEmail(credentials.getEmail()).
                orElseThrow(() -> new EntityNotFoundException("Member not found - " + credentials.getEmail()));

        if (!passwordEncoder.matches(credentials.getOldPassword(), member.getPassword())) {
            return response.fail(ErrorCode.INVALID_OLD_PASSWORD);
        }

        if (!credentials.getNewPassword().equals(credentials.getConfirmNewPassword())) {
            return response.fail(ErrorCode.INVALID_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(credentials.getNewPassword()));

        return response.success(Code.UPDATE_SUCCESS);
    }


    @Transactional
    public ResponseEntity<Body> resetPassword(Credentials credentials) {
        Member member = memberRepository.findByLoginId(credentials.getLoginId()).
                orElseThrow(() -> new EntityNotFoundException("Member not found - " + credentials.getLoginId()));

        String key = "EMAIL:" + credentials.getEmail();
        String passKey = redisTemplate.opsForValue().get(key);

        if (ObjectUtils.isEmpty(passKey) || !passKey.equals(credentials.getPassKey())) {
            return response.fail(ErrorCode.INVALID_PASSWORD_RESET_LINK);
        }

        if (!credentials.getNewPassword().equals(credentials.getConfirmNewPassword())) {
            return response.fail(ErrorCode.INVALID_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(credentials.getNewPassword()));
        redisTemplate.delete(key);

        return response.success(Code.UPDATE_SUCCESS);
    }

    public ResponseEntity<Body> sendResetPasswordMail(ResetPassword resetPassword) {
        String email = resetPassword.getEmail();
        String phone = resetPassword.getPhone();
        if (!memberRepository.existsMemberByEmail(email)) {
            throw new EntityNotFoundException("Member not found - " + email);
        }

        if (!memberRepository.existsMemberByEmailAndMemberInfoPhoneNumber(email, phone)) {
            String errorMessage = String.format("Member with email %s and phone number %s does not exist.", email, phone);
            throw new EntityNotFoundException(errorMessage);
        }

        String passKey = UUID.randomUUID().toString().replace("-", "");

        redisTemplate.opsForValue().set("EMAIL:" + email, passKey, 60 * 5 * 1000L, TimeUnit.MILLISECONDS);

        //todo front 비밀번호 변경 화면
        String authorityLink = "https://" + resetPassword.getAuthorityLink() + "?pass_key=" + passKey + "&email=" + email;

        emailService.resetPassword(email, authorityLink);
        return response.success("메일전송");
    }


}
