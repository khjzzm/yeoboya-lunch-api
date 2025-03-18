package com.yeoboya.lunch.api.v1.member.response;

import com.querydsl.core.annotations.QueryProjection;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberResponse {

    private String loginId;
    private String email;
    private String provider;
    private String name;
    private String bankName;
    private String accountNumber;
    private String bio;
    private String nickName;
    private String phoneNumber;
    private boolean isAccount;
    private String isPrimaryProfileImg;
    private List<ProfileResponse> profileImg;

    @QueryProjection
    public MemberResponse(String loginId, String email, String provider, String name,
                          String bankName, String accountNumber, String bio, String nickName,
                          String phoneNumber){
        this.loginId = loginId;
        this.email = email;
        this.provider = provider;
        this.name = name;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.bio = bio;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
    }

    public MemberResponse(String email, String name, String nickName, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getEmail(), member.getName(),
                member.getMemberInfo().getNickName(), member.getMemberInfo().getPhoneNumber());
    }


}
