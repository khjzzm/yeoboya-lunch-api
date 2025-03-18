package com.yeoboya.lunch.api.v1.member.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseTimeEntity;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountEditor;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long id;

    private String bankName;

    private String accountNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", unique = true)
    private Member member;

    @Builder
    public Account(Member member, String bankName, String accountNumber) {
        this.member = member;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public AccountEditor.AccountEditorBuilder toEditor(){
        return AccountEditor.builder().bankName(bankName).accountNumber(accountNumber);
    }

    public void edit(AccountEditor accountEditor){
        this.bankName = accountEditor.getBankName();
        this.accountNumber = accountEditor.getAccountNumber();
    }


}