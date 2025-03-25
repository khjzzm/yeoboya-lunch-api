package com.yeoboya.lunch.api.v1.board.free.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BoardSearch {

    private long boardId;
    private long parentReplyId;
    private int size;
    private int page;
}
