package com.yeoboya.lunch.api.v1.support.response;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String category;
    private String author;
    private int priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int viewCount;
    private NoticeStatus status;

    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                createSummary(notice.getContent()),
                notice.getCategory(),
                notice.getAuthor(),
                notice.getPriority(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getViewCount(),
                notice.getStatus()
        );
    }


    // ✅ 본문에서 앞 100자만 요약 반환 (또는 ... 처리)
    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}