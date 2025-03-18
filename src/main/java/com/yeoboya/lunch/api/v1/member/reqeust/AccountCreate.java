package com.yeoboya.lunch.api.v1.member.reqeust;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class AccountCreate {

    @NotBlank(message = "은행명을 입력해주세요.")
    private String bankName;

    @NotBlank(message = "계좌번호를 입력해주세요.")
    private String accountNumber;

    @Builder
    public AccountCreate(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }
}
