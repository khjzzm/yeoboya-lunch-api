package com.yeoboya.lunch.config.security.reqeust;

import com.yeoboya.lunch.config.security.constants.Authority;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthorityRequest {

    @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
    private String loginId;

    @NotNull(message = "역할(role)은 필수 입력값입니다.")
    private Authority role;
}

