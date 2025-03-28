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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                provider, userRequest.getClientRegistration().getClientId());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> flatAttributes = flattenAttributes(provider, attributes);

        String loginId = extractId(provider, flatAttributes);
        String email = extractEmail(provider, flatAttributes);
        String name = extractName(provider, flatAttributes);
        String profileImage = extractProfileImage(provider, flatAttributes);

        log.info("Provider: {}, LoginId: {}, Email: {}, Name: {}, ProfileImage: {}", provider, loginId, email, name, profileImage);

        Optional<Member> existingMember = memberRepository.findByEmailAndProvider(email, provider);
        Optional<String> validationMessage = existingMember.flatMap(Member::validateAccountStatus);

        Member member = existingMember.orElseGet(() -> memberRepository.save(
                Member.builder()
                        .loginId(loginId)
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .role(roleRepository.findByRole(Authority.ROLE_GUEST))
                        .build()
        ));

        return new OAuth2UserImpl(oAuth2User.getAuthorities(), flatAttributes, getNameAttributeKey(provider), member, profileImage, validationMessage.orElse(null));
    }

    private Map<String, Object> flattenAttributes(String provider, Map<String, Object> attributes) {
        Map<String, Object> flat = new HashMap<>(attributes);

        if ("naver".equals(provider)) {
            Object response = attributes.get("response");
            if (response instanceof Map) {
                flat.putAll((Map<String, Object>) response);
            }
        } else if ("kakao".equals(provider)) {
            Object account = attributes.get("kakao_account");
            if (account instanceof Map) {
                flat.putAll((Map<String, Object>) account);
                Object profile = ((Map<String, Object>) account).get("profile");
                if (profile instanceof Map) {
                    flat.putAll((Map<String, Object>) profile);
                }
            }
        } else if ("facebook".equals(provider)) {
            Object picture = attributes.get("picture");
            if (picture instanceof Map) {
                Object data = ((Map<String, Object>) picture).get("data");
                if (data instanceof Map) {
                    flat.put("profile_image", ((Map<String, Object>) data).get("url"));
                }
            }
        }

        return flat;
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        return (String) attributes.getOrDefault("email", "");
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        return (String) attributes.getOrDefault("name", attributes.getOrDefault("nickname", ""));
    }

    private String extractProfileImage(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("picture");
            case "github":
                return (String) attributes.get("avatar_url");
            case "facebook":
            case "kakao":
            case "naver":
                return (String) Optional.ofNullable(attributes.get("profile_image"))
                        .orElse(attributes.get("profile_image_url"));
            default:
                return null;
        }
    }

    private String extractId(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("sub");
            case "github":
            case "facebook":
            case "kakao":
            case "naver":
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
            case "facebook":
            case "kakao":
            case "naver":
                return "id";
            default:
                throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
