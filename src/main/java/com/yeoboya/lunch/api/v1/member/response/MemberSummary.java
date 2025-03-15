package com.yeoboya.lunch.api.v1.member.response;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberSummary {
    private final String loginId;
    private final String email;
    private final String name;
    private final String provider;
    private final String roleDesc;

    public MemberSummary(Member member) {
        this.loginId = member.getLoginId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.provider = member.getProvider();
        this.roleDesc = member.getRole() != null ? member.getRole().getRoleDesc() : "N/A";
    }
}
