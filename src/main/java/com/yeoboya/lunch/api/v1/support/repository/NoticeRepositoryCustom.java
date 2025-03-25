package com.yeoboya.lunch.api.v1.support.repository;

import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<NoticeSummaryResponse> searchNotices(NoticeSearchCondition condition, Pageable pageable);
}