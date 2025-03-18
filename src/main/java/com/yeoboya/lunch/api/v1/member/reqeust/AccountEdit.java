package com.yeoboya.lunch.api.v1.member.reqeust;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class AccountEdit {

    private String bankName;
    private String accountNumber;

    @Builder
    public AccountEdit(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

}