package com.yeoboya.lunch.api.v1.board.anonymous.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnonymousBoardResponse {

    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdDate;
    private LocalDateTime deleteAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer viewCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer replyCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer likeCount;

    private int reportCount;

    public static AnonymousBoardResponse from(AnonymousBoard board) {
        return AnonymousBoardResponse.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .nickname(board.getNickname())
                .createdDate(board.getCreatedDate())
                .deleteAt(board.getDeleteAt())
                .viewCount(board.getViewCount())
                .replyCount(board.getReplies() != null ? board.getReplies().size() : 0)
                .likeCount(board.getLikes() != null ? board.getLikes().size() : 0)
                .reportCount(board.getReportCount())
                .build();
    }
}