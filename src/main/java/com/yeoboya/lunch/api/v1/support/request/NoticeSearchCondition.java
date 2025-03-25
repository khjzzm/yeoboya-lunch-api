package com.yeoboya.lunch.api.v1.support.request;

import com.yeoboya.lunch.api.v1.support.constant.NoticeSearchType;
import lombok.Data;

@Data
public class NoticeSearchCondition {
    private NoticeSearchType searchType;
    private String keyword;
}