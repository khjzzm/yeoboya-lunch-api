package com.yeoboya.lunch.api.v1.member.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.member.domain.Account;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.List;


//fixme 가장 공용적으로 쓰이는 dto 제대로 구성 필요함
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

    private Account account;
    private MemberInfo memberInfo;
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
