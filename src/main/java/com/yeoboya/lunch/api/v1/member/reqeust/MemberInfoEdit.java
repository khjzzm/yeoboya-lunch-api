package com.yeoboya.lunch.api.v1.member.reqeust;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MemberInfoEdit {

    private String bio;

    private String nickName;

    private String phoneNumber;

    @Builder
    public MemberInfoEdit(String bio, String nickName, String phoneNumber) {
        this.bio = bio;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
    }
}
