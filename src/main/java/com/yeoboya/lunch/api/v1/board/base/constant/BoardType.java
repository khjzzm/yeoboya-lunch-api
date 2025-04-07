package com.yeoboya.lunch.api.v1.board.base.constant;

import lombok.Getter;

@Getter
public enum BoardType {
    NOTICE("공지사항"),
    FREE("자유게시판"),
    ANON("익명게시판");

    private final String label;

    BoardType(String label) {
        this.label = label;
    }

}