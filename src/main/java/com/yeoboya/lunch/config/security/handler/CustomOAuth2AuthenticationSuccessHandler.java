package com.yeoboya.lunch.config.security.handler;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.UserSecurityStatus;
import com.yeoboya.lunch.config.security.dto.Token;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.service.OAuth2UserImpl;
import com.yeoboya.lunch.config.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    @Value("${front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 인증 성공!");

        //  OAuth2UserImpl 가져오기 (CustomOAuth2UserService loadUser)
        OAuth2UserImpl oAuth2User = (OAuth2UserImpl) authentication.getPrincipal();
        String loginId = oAuth2User.getMember().getLoginId();
        String email = oAuth2User.getMember().getEmail();
        String name = oAuth2User.getMember().getName();
        String provider = oAuth2User.getMember().getProvider();
        String profileImage = oAuth2User.getProfileImage();

        log.error("oAuth2User {}", oAuth2User);

        //  회원가입 여부 확인
        Optional<Member> existingMember = memberRepository.findByEmailAndProvider(email, provider);
        boolean isNewUser = existingMember.isEmpty(); // 완전 신규 회원
        boolean isGuest = existingMember.isPresent() && existingMember.get().getRole().getRole().equals(Authority.ROLE_GUEST); // 인증만끝낸상태

        if (isNewUser) {
            memberRepository.save(oAuth2User.getMember());
        }
        //  신규 회원이면 추가 정보 입력 페이지로 이동
        String redirectURL;
        if (isNewUser || isGuest) {
            // 게스트거나 신규 유저라면 → 추가 정보 입력 페이지로 이동
            redirectURL = UriComponentsBuilder.fromUriString(frontUrl + "/user/signup/social")
                    .queryParam("isNewUser", true)
                    .queryParam("isGuest", isGuest)
                    .queryParam("loginId", loginId)
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("provider", provider)
                    .queryParam("picture", profileImage)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        } else {
            //  JWT 토큰 발급
            Token token = jwtTokenProvider.generateToken(authentication, provider);
            Cookie providerCookie = CookieUtils.createSecureHttpOnlyCookie("provider", token.getIssuer(), false);
            Cookie accessTokenCookie = CookieUtils.createSecureHttpOnlyCookie("token", token.getAccessToken(), false);
            Cookie refreshTokenCookie = CookieUtils.createSecureHttpOnlyCookie("refreshToken", token.getRefreshToken(), false);

            CookieUtils.addCookieToResponse(response, providerCookie, "None");
            CookieUtils.addCookieToResponse(response, accessTokenCookie, "None");
            CookieUtils.addCookieToResponse(response, refreshTokenCookie, "None");

            log.error("2 {}", token.getRefreshToken());
            //  Redis에 RefreshToken 저장
            redisTemplate.opsForValue().set("RT:" + email,
                    token.getRefreshToken(),
                    token.getRefreshTokenExpirationTime() - new Date().getTime(),
                    TimeUnit.MILLISECONDS);

            redirectURL = UriComponentsBuilder.fromUriString(frontUrl)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
