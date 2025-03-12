package com.yeoboya.lunch.api.v1.member.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import com.yeoboya.lunch.config.security.constants.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberRoleResponse {

    private String loginId;
    private String email;
    private String provider;
    private String name;
    private String roleDesc;
    private Authority authority;
    private boolean isEnabled;
    private boolean isAccountNonLocked;

    @QueryProjection
    public MemberRoleResponse(String loginId, String email, String provider, String name, String roleDesc, Authority authority, boolean isEnabled, boolean isAccountNonLocked){
        this.loginId = loginId;
        this.email = email;
        this.provider = provider;
        this.name = name;
        this.roleDesc = roleDesc;
        this.authority = authority;
        this.isEnabled = isEnabled;
        this.isAccountNonLocked = isAccountNonLocked;
    }

}
