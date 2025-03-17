package com.yeoboya.lunch.api.v1.member.reqeust;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchMember {
    private String loginId;
    private String name;
    private String nickName;
}
