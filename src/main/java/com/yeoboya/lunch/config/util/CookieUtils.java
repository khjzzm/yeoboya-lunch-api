package com.yeoboya.lunch.config.util;

import com.yeoboya.lunch.config.security.dto.Token;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class CookieUtils {

    public static Cookie createSecureHttpOnlyCookie(String name, String value, String activeProfile) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        if (activeProfile.startsWith("prod")) {
            cookie.setDomain(".yeoboya-lunch.com");
        }
        cookie.setMaxAge(7 * 24 * 60 * 60); // 1주일
        return cookie;
    }


    public static void addCookieToResponse(HttpServletResponse response, Cookie cookie) {
        String sameSite = "None"; // 기본 설정
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        sb.append("Path=").append(cookie.getPath()).append("; ");
        sb.append("Max-Age=").append(cookie.getMaxAge()).append("; ");
        sb.append("HttpOnly; Secure; SameSite=").append(sameSite);
        if (cookie.getDomain() != null) {
            sb.append("; Domain=").append(cookie.getDomain());
        }

        response.addHeader("Set-Cookie", sb.toString());
    }

    public static Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    public static void setAuthCookies(HttpServletResponse response, Token token , String activeProfile) {
        Cookie accessTokenCookie = createSecureHttpOnlyCookie("token", token.getAccessToken(), activeProfile);
        Cookie refreshTokenCookie = createSecureHttpOnlyCookie("refreshToken", token.getRefreshToken(), activeProfile);

        addCookieToResponse(response, accessTokenCookie);
        addCookieToResponse(response, refreshTokenCookie);
    }



    public static void deleteAuthCookies(HttpServletResponse response) {
        Cookie tokenCookie = deleteCookie("token");
        Cookie refreshTokenCookie = deleteCookie("refreshToken");

        addCookieToResponse(response, tokenCookie);
        addCookieToResponse(response, refreshTokenCookie);
    }

    public static Cookie getCookie(String name) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}