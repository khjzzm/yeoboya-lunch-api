package com.yeoboya.lunch.config.security.constants;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
public enum Authority implements GrantedAuthority {

    ROLE_ADMIN("어드민"),
    ROLE_MANAGER("매니저"),
    ROLE_USER("유저"),
    ROLE_GUEST("게스트"),
    ROLE_BLOCK("차단"),
    ROLE_WITHDRAWN("탈퇴");

    private final String koreanName;

    Authority(String koreanName) {
        this.koreanName = koreanName;
    }

    @Override
    public String getAuthority() {
        return name();
    }

    /**
     * 한글명으로 Authority 찾기
     */
    public static Authority fromKoreanName(String koreanName) {
        return Arrays.stream(Authority.values())
                .filter(auth -> auth.koreanName.equals(koreanName))
                .findFirst()
                .orElse(null); // 존재하지 않으면 null 반환
    }
}