package com.yeoboya.lunch.api.v1.support.response;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NoticeDetailResponse {
    private Long boardId;
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private boolean hasLiked;
    private String author;
    private final Boolean pinned;
    private LocalDate startDate;
    private LocalDate endDate;
    private NoticeStatus status;

    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCategory().getName(),
                notice.getViewCount(),
                false,
                notice.getAuthor(),
                notice.getPinned(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getStatus()
        );
    }

    public static NoticeDetailResponse from(Notice notice, boolean hasLiked) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCategory().getName(),
                notice.getViewCount(),
                hasLiked,
                notice.getAuthor(),
                notice.getPinned(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getStatus()
        );
    }
}