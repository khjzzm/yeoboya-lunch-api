package com.yeoboya.lunch.api.v1.support.request;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardSearchType;
import lombok.Data;

@Data
public class NoticeSearchCondition {

    private long boardId;
    private BoardSearchType searchType;
    private String keyword;
}