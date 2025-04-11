package com.yeoboya.lunch.api.v1.member.domain;

import com.yeoboya.lunch.api.v1.member.reqeust.MemberInfoEditor;
import com.yeoboya.lunch.api.v1.member.validation.Phone;
import com.yeoboya.lunch.config.security.reqeust.UserRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class MemberInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_INFO_ID", nullable = false)
    private Long id;

    @Phone
    private String phoneNumber = "010-0000-0000";

    @Column(unique = true)
    private String nickName = "#" + (int) (Math.random() * 100000);

    @Column(nullable = false)
    private String bio = "";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public static MemberInfo createMemberInfo(UserRequest.SignUp signUp, Member member) {
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setNickName(signUp.getNickName());
        memberInfo.setMember(member);
        return memberInfo;
    }

    public MemberInfoEditor.MemberInfoEditorBuilder toEditor() {
        return MemberInfoEditor
                .builder()
                .bio(bio)
                .phoneNumber(phoneNumber);
    }

    public void edit(MemberInfoEditor memberInfoEditor){
        this.bio = memberInfoEditor.getBio();
        this.phoneNumber = memberInfoEditor.getPhoneNumber();
    }
}