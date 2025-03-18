package com.yeoboya.lunch.api.v1.member.service;


import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.member.domain.Account;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.AccountRepository;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountCreate;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountEdit;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountEditor;
import com.yeoboya.lunch.api.v1.member.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse addAccount(Member member, AccountCreate accountCreate) {
        Account createAccount = Account.builder()
                .member(member)
                .bankName(accountCreate.getBankName())
                .accountNumber(accountCreate.getAccountNumber())
                .build();

        Account account = accountRepository.save(createAccount);
        return AccountResponse.from(account);
    }

    @Transactional
    public void editAccount(Member member, AccountEdit edit) {

        // 계좌가 없는 경우 예외 발생
        if (member.getAccount() == null) {
            throw new EntityNotFoundException("계좌 정보가 존재하지 않습니다.");
        }

        Account account = member.getAccount();

        AccountEditor accountEditor = account.toEditor()
                .accountNumber(edit.getAccountNumber())
                .bankName(edit.getBankName())
                .build();

        try {
            account.edit(accountEditor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
