package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RoleRepository roleRepository;


    /**
     * 소셜 로그인
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, userRequest.getClientRegistration().getClientId());

        // 제공자별 데이터 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String loginId = extractId(provider, attributes);
        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String profileImage = extractProfileImage(provider, attributes);

        log.error("{}", attributes);
        log.error("p {}, l {}, e {}, n {} pi {}", provider, loginId, email, name, profileImage);

        Optional<Member> existingMember = memberRepository.findByEmailAndProvider(email, provider);
        boolean isNewUser = existingMember.isEmpty();

        Member member;
        if (isNewUser) {
            member = Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .name(name)
                    .provider(provider)
//                    .providerId(attributes) //todo 이름변경 타입 크기늘리기
                    .role(roleRepository.findByRole(Authority.ROLE_GUEST)) // 기본적으로 GUEST 권한
                    .build();
        } else {
            member = existingMember.get();
        }

        return new OAuth2UserImpl(oAuth2User.getAuthorities(), attributes, getNameAttributeKey(provider), member, profileImage);
    }


    // 이메일
    private String extractEmail(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("email");
            case "github":
                return (String) attributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

                if (email == null || email.isEmpty()) {
                    return "";
                }

                return email;
            case "naver":
                return (String) ((Map<String, Object>) attributes.get("response")).get("email");
            default:
                return null;
        }
    }

    // 이름
    private String extractName(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("name");
            case "github":
                return (String) attributes.get("name");
            case "kakao":
                return (String) ((Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("nickname");
            case "naver":
                return (String) ((Map<String, Object>) attributes.get("response")).get("name");
            default:
                return null;
        }
    }

    // 프로필 이미지
    private String extractProfileImage(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("picture");
            case "github":
                return (String) attributes.get("avatar_url");
            case "kakao":
                return (String) ((Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("profile_image_url");
            case "naver":
                return (String) ((Map<String, Object>) attributes.get("response")).get("profile_image");
            default:
                return null;
        }
    }

    // 아이디로 사용될 값
    private String extractId(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("sub");
            case "github":
                return String.valueOf(attributes.get("id"));
            case "kakao":
                return String.valueOf(attributes.get("id"));
            default:
                return null;
        }
    }

    private String getNameAttributeKey(String provider) {
        switch (provider) {
            case "google":
                return "sub";
            case "github":
                return "id";
            case "kakao":
                return "id";
            case "naver":
                return "id";
            default:
                throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}