package com.yeoboya.lunch.config.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class CookieUtils {

    public static Cookie createSecureHttpOnlyCookie(String name, String value, boolean isProd) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setPath("/");
        if (isProd) {
            cookie.setDomain("yeoboya-lunch.com");
        }
        cookie.setMaxAge(7 * 24 * 60 * 60); // 1주일 유효
        return cookie;
    }

    public static void addCookieToResponse(HttpServletResponse response, Cookie cookie, String sameSite) {
        response.addCookie(cookie);
        String cookieHeader = String.format("%s=%s; Path=%s; Domain=%s; Max-Age=%d; HttpOnly; Secure",
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getDomain(),
                cookie.getMaxAge(),
                sameSite
        );
        response.addHeader("Set-Cookie", cookieHeader);
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
