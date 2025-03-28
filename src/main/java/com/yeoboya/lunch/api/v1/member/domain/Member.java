package com.yeoboya.lunch.api.v1.member.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseTimeEntity;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.config.pricingPlan.domain.ApiKey;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.UserSecurityStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"email", "provider"}
        )
})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID", nullable = false)
    private Long id;

    @Column(unique = true)
    private String loginId;

    @Column
    private String email;

    private String name;

    private String password;

    @ManyToOne
    private Role role;

    @OneToOne(mappedBy = "member")
    private Account account;

    @OneToOne(mappedBy = "member", cascade = CascadeType.PERSIST)
    private MemberInfo memberInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<LoginInfo> loginInfos;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MemberProfileFile> memberProfileFiles = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private ApiKey apiKey;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private UserSecurityStatus userSecurityStatus;

    @Column
    private String provider;

    private String providerId;

    //연관관계 편의 메소드
    public static Member createMember(Member pMember, MemberInfo memberInfo, Role role, UserSecurityStatus userSecurityStatus){
        Member member = new Member();
        member.setEmail(pMember.getEmail());
        member.setLoginId(pMember.getLoginId());
        member.setName(pMember.getName());
        member.setProvider(pMember.getProvider());
        member.setProviderId(pMember.getProviderId());
        member.setPassword(pMember.getPassword());
        member.setRole(role);
        member.addMemberInfo(memberInfo);
        member.addUserSecurityStatus(userSecurityStatus);
        return member;
    }

    public void addMemberInfo(MemberInfo memberInfo){
        this.setMemberInfo(memberInfo);
        memberInfo.setMember(this);
    }

    public void addUserSecurityStatus(UserSecurityStatus userSecurityStatus){
        this.setUserSecurityStatus(userSecurityStatus);
        userSecurityStatus.setMember(this);
    }


    //소셜로그인 계정 상태 체크
    public Optional<String> validateAccountStatus() {
        if (this.userSecurityStatus == null) {
            return Optional.of("계정 상태 정보가 존재하지 않습니다.");
        }

        if (!this.userSecurityStatus.isAccountNonLocked()) {
            return Optional.of("계정이 잠겨 있습니다. 관리자에게 문의하세요.");
        }

        if (!this.userSecurityStatus.isEnabled()) {
            return Optional.of("계정이 비활성화 상태입니다. 관리자에게 문의하세요.");
        }

        return Optional.empty();
    }

}
