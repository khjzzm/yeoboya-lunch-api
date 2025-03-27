package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Getter
public class OAuth2UserImpl extends DefaultOAuth2User {

    private final Member member; // already set loginId, email, name, provider, role
    private final String profileImage;
    private final String accountStatusMessage;

    public OAuth2UserImpl(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey,
                          Member member, String profileImage, String accountStatusMessage) {
        super(authorities, attributes, nameAttributeKey);
        this.member = member;
        this.profileImage = profileImage;
        this.accountStatusMessage = accountStatusMessage;
    }

    public boolean hasAccountIssue() {
        return accountStatusMessage != null;
    }

}