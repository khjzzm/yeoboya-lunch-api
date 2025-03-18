package com.yeoboya.lunch.api.v1.member.reqeust;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AccountEditor {

    private String bankName;
    private String accountNumber;

    public AccountEditor(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public static AccountEditorBuilder builder() {
        return new AccountEditorBuilder();
    }

    public static class AccountEditorBuilder {
        private String bankName;
        private String accountNumber;

        public AccountEditorBuilder bankName(final String bankName) {
            if (bankName != null) {
                this.bankName = bankName;
            }
            return this;
        }

        public AccountEditorBuilder accountNumber(final String accountNumber) {
            if (accountNumber != null) {
                this.accountNumber = accountNumber;
            }
            return this;
        }

        public AccountEditor build() {
            return new AccountEditor(this.bankName, this.accountNumber);
        }

        public String toString() {
            return "AccountEditor.AccountEditorBuilder(bankName=" + this.bankName + ", accountNumber=" + this.accountNumber + ")";
        }
    }
}
