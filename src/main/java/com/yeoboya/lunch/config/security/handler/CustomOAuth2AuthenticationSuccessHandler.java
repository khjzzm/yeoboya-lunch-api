package com.yeoboya.lunch.config.security.handler;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.dto.Token;
import com.yeoboya.lunch.config.security.service.OAuth2UserImpl;
import com.yeoboya.lunch.config.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    @Value("${spring.profiles.active}")
    private String activeProfile;

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

        Optional<Member> existingMember = memberRepository.findByLoginIdAndProvider(loginId, provider);
        boolean isNewUser = existingMember.isEmpty();   // 완전 신규회원
        boolean isGuest = existingMember.map(m -> m.getRole().getRole().equals(Authority.ROLE_GUEST)).orElse(false);    // 게스트 회원
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(frontUrl + (isNewUser || isGuest ? "/user/signup/social" : ""));

        if (isNewUser || isGuest) {
            builder.queryParam("isNewUser", true)
                    .queryParam("isGuest", isGuest)
                    .queryParam("loginId", loginId)
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("provider", provider)
                    .queryParam("picture", profileImage);
        } else {
            Token token = jwtTokenProvider.generateToken(authentication);
            CookieUtils.setAuthCookies(response, token, activeProfile);

            redisTemplate.opsForValue().set("RT:" + loginId,
                    token.getRefreshToken(),
                    token.getRefreshTokenExpirationTime() - new Date().getTime(),
                    TimeUnit.MILLISECONDS);
        }
        String redirectURL = builder.build().encode(StandardCharsets.UTF_8).toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
