package com.yeoboya.lunch.api.v1.board.free.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FreeBoardProjection {

    private final Long boardId;
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


    @QueryProjection
    public FreeBoardProjection(Long boardId, String title, String content, String summary, String category,
                               boolean secret, String loginId, String name, int viewCount,
                               long likeCount, long replyCount) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.category = category;
        this.secret = secret;
        this.loginId = loginId;
        this.name = name;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.replyCount = replyCount;

    }
}