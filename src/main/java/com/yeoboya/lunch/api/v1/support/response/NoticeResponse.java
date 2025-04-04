package com.yeoboya.lunch.api.v1.support.response;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String summary;
    private final String category;
    private final String author;
    private final Boolean pinned;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int viewCount;
    private final NoticeStatus status;
    private final long likeCount;
    private final long replyCount;
    private final boolean hasFile;
    private final LocalDateTime createdDate;

    public static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

    // 생성자에서 content와 summary 처리
    public NoticeResponse(Long id, String title, String content, String category, String author,
                          boolean pinned, LocalDate startDate, LocalDate endDate, int viewCount, NoticeStatus status,
                          long likeCount, long replyCount, boolean hasFile, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = createSummary(content);
        this.category = category;
        this.author = author;
        this.pinned = pinned;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCount = viewCount;
        this.status = status;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.hasFile = hasFile;
        this.createdDate = createdDate;
    }
}