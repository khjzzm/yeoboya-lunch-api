package com.yeoboya.lunch.config.security.dto;

import com.yeoboya.lunch.api.v1.common.exception.AuthorityException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Users implements UserDetails {

    private static final long serialVersionUID = 1L;
    private String loginId;
    private String password;
    private Boolean lock;
    private Boolean enabled;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public String getPassword(){
        return this.password;
    }


    // 계정이 잠겨있지 않은지 여부를 반환합니다
    // 운영자가 불법/비정상 사용자 차단
    // 로그인 시도 실패 5회 이상 → 계정 자동 잠금
    @Override
    public boolean isAccountNonLocked() {
        if(!this.lock) {
            throw new AuthorityException("사용자 계정이 잠겨 있습니다. 관리자에게 문의하세요.");
        }
        return true;
    }

    // 계정이 활성화(사용 가능) 상태인지 여부를 반환합니다.
    // 이메일 인증을 아직 하지 않은 사용자
	// 탈퇴한 사용자 비활성 처리
    @Override
    public boolean isEnabled() {
        if(!this.enabled) {
            throw new AuthorityException("계정이 비활성화 상태입니다. 관리자에게 문의하세요.");
        }
        return true;
    }

    // 계정이 만료되지 않았는지 여부를 반환합니다.
    // 유료 서비스에서 기간 제한 (ex. 1년 계약 등)
	// 테스트용 계정 또는 체험판 계정
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 인증 정보(주로 비밀번호)가 만료되지 않았는지 여부를 반환합니다
    // 보안을 위해 주기적으로 비밀번호 변경 강제
	// 사내 시스템에서 90일 주기 비밀번호 변경 정책
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
