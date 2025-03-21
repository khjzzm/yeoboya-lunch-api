package com.yeoboya.lunch.config.security;

import com.yeoboya.lunch.config.security.dto.Token;
import com.yeoboya.lunch.config.security.reqeust.ClientRequestInfo;
import com.yeoboya.lunch.config.security.service.CustomAuthenticationDetailsBuilder;
import com.yeoboya.lunch.config.security.service.UserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yeoboya.lunch.config.aws.AwsSecretsManagerClient.getSecret;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;
    private final CustomAuthenticationDetailsBuilder customAuthenticationDetailsBuilder;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = TimeUnit.DAYS.toMillis(5);   // 5일
    private static final long REFRESH_TOKEN_EXPIRE_TIME = TimeUnit.DAYS.toMillis(14);    // 14일
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(CustomAuthenticationDetailsBuilder customAuthenticationDetailsBuilder, UserDetailsService userDetailsService) {
        this.customAuthenticationDetailsBuilder = customAuthenticationDetailsBuilder;
        this.userDetailsService = userDetailsService;
        String secretKey = getSecret("prod/lunch").getString("jwt.token.secretkey");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public Token generateToken(Authentication authentication, String provider) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setId(String.valueOf(UUID.randomUUID()))
                .setIssuer(provider)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(accessTokenExpiresIn)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshAccessTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshAccessTokenExpiresIn)
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Claims claims = this.parseClaims(accessToken);
        return Token.builder()
                .subject(authentication.getName())
                .id(claims.getId())
                .issuer(claims.getIssuer())
                .issueDAt(String.valueOf(claims.getIssuedAt()))
                .accessToken(accessToken)
                .tokenExpirationTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(accessTokenExpiresIn))
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshAccessTokenExpiresIn.getTime())
                .refreshTokenExpirationTimeStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(refreshAccessTokenExpiresIn))
                .build();
    }


    public Token generateToken(String loginId, String provider, Collection<? extends GrantedAuthority> authorities) {
        String authorityStr = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(loginId)
                .setId(String.valueOf(UUID.randomUUID()))
                .setIssuer(provider)
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .claim(AUTHORITIES_KEY, authorityStr)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setSubject(loginId)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Claims claims = this.parseClaims(accessToken);
        return Token.builder()
                .subject(loginId)
                .id(claims.getId())
                .issuer(claims.getIssuer())
                .issueDAt(String.valueOf(claims.getIssuedAt()))
                .accessToken(accessToken)
                .tokenExpirationTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(accessTokenExpiresIn))
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshTokenExpiresIn.getTime())
                .refreshTokenExpirationTimeStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(refreshTokenExpiresIn))
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token, HttpServletRequest request) {
        Claims claims = this.parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(String.valueOf(claims.get(AUTHORITIES_KEY)).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserSecurityStatus 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, token, authorities);

        ClientRequestInfo clientRequestInfo = customAuthenticationDetailsBuilder.buildDetails(request);
        authenticationToken.setDetails(clientRequestInfo);

        return authenticationToken;
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = this.parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(String.valueOf(claims.get(AUTHORITIES_KEY)).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthenticationWithLoadUserByUsername(String refreshToken) {
        Claims claims = this.parseClaims(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, refreshToken, userDetails.getAuthorities());
    }


    //토큰 유효성검사
    public boolean validateToken(String token) {
        return this.parseClaims(token) != null;
    }

    //토큰 parse
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (SecurityException e) {
            log.error("보안 예외가 발생하였습니다.");
            throw new JwtException("보안 예외가 발생하였습니다: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("잘못 형식화된 JWT 예외가 발생하였습니다.");
            throw new JwtException("잘못 형식화된 JWT 예외가 발생하였습니다: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 예외가 발생하였습니다.");
            throw new JwtException("지원하지 않는 JWT 예외가 발생하였습니다: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 예외가 발생하였습니다.");
            throw new JwtException("만료된 JWT 예외가 발생하였습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인자 예외가 발생하였습니다.");
            throw new JwtException("잘못된 인자 예외가 발생하였습니다: " + e.getMessage());
        }
    }

    // 토큰 유효기간 확인
    public Long getExpiration(String accessToken) throws ExpiredJwtException {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    // Request Header 에서 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // SecurityContext 에 담겨 있는 authentication userLoginId 정보
    public static String getCurrentUserLoginId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information.");
        }
        return authentication.getName();
    }

    public String getJwtTokenSubject(HttpServletRequest request) {
        String token = this.resolveToken(request);
        Claims claims = this.parseClaims(token);
        return claims.getSubject();
    }

}
