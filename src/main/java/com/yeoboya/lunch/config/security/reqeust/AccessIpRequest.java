package com.yeoboya.lunch.config.security.reqeust;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class AccessIpRequest {

    private String ipAddress;
    private boolean block;
    private String reason;
    private OffsetDateTime expiresAt;
    private int hitCount;
}