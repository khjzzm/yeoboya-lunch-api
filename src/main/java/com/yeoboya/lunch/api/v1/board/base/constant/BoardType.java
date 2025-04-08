package com.yeoboya.lunch.api.v1.board.base.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonValue
    public String getName() {
        return this.name().toLowerCase(); // 프론트로 보낼 때 "notice", "free", "anon"
    }

    @JsonCreator
    public static BoardType from(String value) {
        for (BoardType type : BoardType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BoardType: " + value);
    }
}