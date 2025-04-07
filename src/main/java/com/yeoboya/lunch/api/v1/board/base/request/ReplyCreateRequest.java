package com.yeoboya.lunch.api.v1.board.base.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ReplyCreateRequest {

    @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
    private String loginId;
    private Long boardNo;
    private String content;
    private Long parentReplyId;

    @Override
    public String toString() {
        return "ReplyCreateRequest{" +
                "loginId='" + loginId + '\'' +
                ", boardNo=" + boardNo +
                ", content='" + content + '\'' +
                ", parentReplyId=" + parentReplyId +
                '}';
    }
}
