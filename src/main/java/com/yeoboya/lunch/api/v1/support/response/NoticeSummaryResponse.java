package com.yeoboya.lunch.api.v1.support.response;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeSummaryResponse {
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
    private long likeCount;
    private long replyCount;

    public static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

    // 생성자에서 content와 summary 처리
    public NoticeSummaryResponse(Long id, String title, String content, String category, String author,
                                 int priority, LocalDateTime startDate, LocalDateTime endDate, int viewCount,
                                 NoticeStatus status, long likeCount, long replyCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = createSummary(content);  // summary 계산
        this.category = category;
        this.author = author;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCount = viewCount;
        this.status = status;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
    }
}