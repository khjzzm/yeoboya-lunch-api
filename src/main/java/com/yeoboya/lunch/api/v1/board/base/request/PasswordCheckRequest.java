package com.yeoboya.lunch.api.v1.board.base.request;

import lombok.Getter;

@Getter
public class PasswordCheckRequest {

    private Long boardNo;
    private String password;
}
