package com.yeoboya.lunch.config.security.reqeust;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccessIpRequest {

    private String ipAddress;
    private boolean block;
    private String reason;
    private LocalDateTime expiresAt;
    private int hitCount;
}