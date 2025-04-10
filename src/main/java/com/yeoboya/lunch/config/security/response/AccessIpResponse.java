package com.yeoboya.lunch.config.security.response;

import com.yeoboya.lunch.config.security.domain.AccessIp;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Builder
public class AccessIpResponse {

    private Long id;
    private String ipAddress;
    private boolean block;
    private String reason;
    private OffsetDateTime expiresAt;
    private int hitCount;

    public static AccessIpResponse from(AccessIp entity) {
        return AccessIpResponse.builder()
                .id(entity.getId())
                .ipAddress(entity.getIpAddress())
                .block(entity.isBlock())
                .reason(entity.getReason())
                .expiresAt(entity.getExpiresAt())
                .hitCount(entity.getHitCount())
                .build();
    }
}