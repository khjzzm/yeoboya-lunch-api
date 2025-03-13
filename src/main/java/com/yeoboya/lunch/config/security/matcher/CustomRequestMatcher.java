package com.yeoboya.lunch.config.security.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.Set;

public class CustomRequestMatcher implements RequestMatcher {

    private final String urlPattern;
    private final Set<HttpMethod> allowedMethods;

    public CustomRequestMatcher(String urlPattern, Set<HttpMethod> allowedMethods) {
        this.urlPattern = urlPattern;
        this.allowedMethods = allowedMethods.isEmpty() ? EnumSet.allOf(HttpMethod.class) : allowedMethods;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURI();

        boolean isUrlMatched = requestUrl.matches(urlPattern);
        boolean isMethodMatched = allowedMethods.contains(HttpMethod.valueOf(requestMethod));

        return isUrlMatched && isMethodMatched;
    }
}