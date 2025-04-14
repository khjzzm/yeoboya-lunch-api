package com.yeoboya.lunch.api.v1.member.service;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.reqeust.UserRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DummyMemberFactory {

    public static Member createWithdrawnMember(UserRequest.WithdrawRequest withdrawRequest, Role role) {
        return Member.builder()
                .loginId("withdrawn-" + UUID.randomUUID())
                .email("withdrawn-" + System.currentTimeMillis() + "@dummy.com")
                .provider(withdrawRequest.getProvider())
                .name(withdrawRequest.getNickName())
                .password(null)
                .role(role)
                .account(null)
                .build();
    }
}