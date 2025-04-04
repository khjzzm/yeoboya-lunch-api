package com.yeoboya.lunch.api.v1.support.repository.notice;

import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<NoticeResponse> searchNotices(NoticeSearchCondition condition, Pageable pageable);
}