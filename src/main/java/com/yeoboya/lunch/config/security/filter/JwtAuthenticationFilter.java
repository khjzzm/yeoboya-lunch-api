package com.yeoboya.lunch.config.security.filter;

import com.yeoboya.lunch.config.security.JwtTokenProvider;
import com.yeoboya.lunch.config.security.repository.TokenIgnoreUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenIgnoreUrlRepository tokenIgnoreUrlRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        if (!shouldIgnore(request)) {
            if (token != null) {
                // Redis에서 해당 토큰이 로그아웃된 상태인지 확인
                String isLogout = redisTemplate.opsForValue().get("LOT:" + token);
                if (!ObjectUtils.isEmpty(isLogout)) { // 로그아웃된 토큰이면 요청 차단
                    log.warn("🚨 로그아웃된 토큰 요청 차단: {}", token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("로그아웃된 토큰입니다.");
                    return;
                }

                // 유효한 토큰이면 인증 처리
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token, request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("=================================  토큰 컨텍스트에서 통과 정보  ============================================");
                    log.debug(authentication.getPrincipal() + " : " + authentication);
                    log.debug(token);
                    log.debug("=====================================================================================================");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldIgnore(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        String uri = request.getRequestURI();

        return tokenIgnoreUrlRepository.getTokenIgnoreUrls()
                .stream()
                .anyMatch(r -> matcher.match(r.getUrl(), uri) && Boolean.TRUE.equals(r.getIsIgnore()));
    }

}
