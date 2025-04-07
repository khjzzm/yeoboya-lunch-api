package com.yeoboya.lunch.api.v1.board.base.response;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardTypeResponse {
    private String code;
    private String name;

    public static BoardTypeResponse from(BoardType boardType) {
        String displayName;
        switch (boardType) {
            case NOTICE:
            case ANON:
            case FREE:
                displayName = boardType.getLabel();
                break;
            default:
                displayName = "기타";
        }
        return new BoardTypeResponse(boardType.name(), displayName);
    }
}