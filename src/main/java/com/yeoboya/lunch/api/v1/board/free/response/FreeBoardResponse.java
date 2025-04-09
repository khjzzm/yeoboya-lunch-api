package com.yeoboya.lunch.api.v1.board.free.response;

import com.querydsl.core.annotations.QueryProjection;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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
    private final boolean hasFile;

    private List<HashTagResponse> hashTag;

    //DB (substring)
    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

    @QueryProjection
    public FreeBoardResponse(Long boardNo, String title, String content, String category,
                             boolean secret, String loginId, String name, int viewCount,
                             long likeCount, long replyCount, LocalDateTime createdDate, boolean hasFile) {
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
        this.hasFile = hasFile;
    }
}