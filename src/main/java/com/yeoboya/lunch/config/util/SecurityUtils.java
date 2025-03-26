package com.yeoboya.lunch.config.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {

    /**
     * 현재 인증된 사용자의 loginId를 반환합니다.
     * 로그인하지 않은 경우 null 반환
     *
     * @return 로그인된 사용자의 loginId 또는 null
     */
    public static Optional<String> getCurrentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        }

        if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    /**
     * 현재 사용자가 주어진 loginId와 일치하는지 확인합니다.
     * 비로그인 상태면 false
     *
     * @param loginId 비교할 아이디
     * @return 일치하면 true, 아니면 false
     */
    public static boolean isCurrentUser(String loginId) {
        return getCurrentUserLoginId()
                .map(currentUser -> currentUser.equals(loginId))
                .orElse(false); // 로그인 안 되어 있으면 false
    }
}