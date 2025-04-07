package com.yeoboya.lunch.api.v1.board.free.request;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardSearchType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BoardSearchCondition {

    private long boardNo;
    private BoardSearchType searchType;
    private String keyword;
}
