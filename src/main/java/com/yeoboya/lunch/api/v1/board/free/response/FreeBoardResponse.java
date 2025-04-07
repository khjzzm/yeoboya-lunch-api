package com.yeoboya.lunch.api.v1.board.free.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FreeBoardResponse {
    private final Long boardNo;
    private final String title;
    private final String content;
    private final String summary;
    private final String category;
    private final boolean secret;
    private final String loginId;
    private final String name;
    private final int viewCount;
    private final long likeCount;
    private final long replyCount;
    private final LocalDateTime createdDate;


    //DB (substring)
    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

    @QueryProjection
    public FreeBoardResponse(Long boardNo, String title, String content, String category,
                             boolean secret, String loginId, String name, int viewCount,
                             long likeCount, long replyCount, LocalDateTime createdDate) {
        this.boardNo = boardNo;
        this.title = title;
        this.content = content;
        this.summary = createSummary(content);
        this.category = category;
        this.secret = secret;
        this.loginId = loginId;
        this.name = name;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.createdDate = createdDate;

    }
}