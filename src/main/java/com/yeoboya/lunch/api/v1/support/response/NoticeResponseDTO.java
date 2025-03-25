package com.yeoboya.lunch.api.v1.support.response;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeResponseDTO {
    private Long id;
    private String title;
    private String summary; // content 대신 summary
    private String category;
    private String author;
    private int priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int viewCount;
    private NoticeStatus status;
    private boolean isRead;

    public static NoticeResponseDTO from(Notice notice, boolean isRead) {
        return new NoticeResponseDTO(
                notice.getId(),
                notice.getTitle(),
                createSummary(notice.getContent()),
                notice.getCategory(),
                notice.getAuthor(),
                notice.getPriority(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getViewCount(),
                notice.getStatus(),
                isRead
        );
    }

    // ✅ 본문에서 앞 100자만 요약 반환 (또는 ... 처리)
    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}