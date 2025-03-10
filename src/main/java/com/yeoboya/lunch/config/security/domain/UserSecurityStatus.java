package com.yeoboya.lunch.config.security.domain;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UserSecurityStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_security_status_id", nullable = false)
    private Long id;

    private boolean isAccountNonExpired = true;         //만료
    private boolean isAccountNonLocked = true;          //잠김 (사용중)
    private boolean isCredentialsNonExpired = true;     //비밀번호(자격 증명) 만료
    private boolean isEnabled = true;                   //활성화여부 (사용중)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public static UserSecurityStatus createUserSecurityStatus(Member member) {
        UserSecurityStatus userSecurityStatus = new UserSecurityStatus();
        userSecurityStatus.setMember(member);
        return userSecurityStatus;
    }

}
