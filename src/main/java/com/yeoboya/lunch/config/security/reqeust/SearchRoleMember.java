package com.yeoboya.lunch.config.security.reqeust;

import com.yeoboya.lunch.api.v1.member.reqeust.SearchMember;
import com.yeoboya.lunch.config.security.constants.Authority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchRoleMember extends SearchMember {
    private List<Authority> authority;
}
