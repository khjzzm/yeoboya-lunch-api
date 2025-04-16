package com.yeoboya.lunch.api.v1.board.anonymous.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Builder
public class AnonymousBoardResponse {

    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdDate;
    private OffsetDateTime deleteAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer viewCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer replyCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer likeCount;

    private int reportCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remainingTime;

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
                .remainingTime(formatRemainingTime(board.getDeleteAt()))
                .build();
    }

    private static String formatRemainingTime(OffsetDateTime deleteAt) {
        if (deleteAt == null) return null;

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("+09:00"));
        Duration duration = Duration.between(now, deleteAt);

        if (duration.isNegative()) {
            return "만료됨";
        }

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (days >= 30) {
            return "한 달 이상";
        } else if (days >= 7) {
            return "일주일 이상";
        } else if (days >= 3) {
            return "3일 이상";
        } else if (days >= 1) {
            return "하루 이상";
        } else if (hours >= 24) {
            return "24시간 이내";
        } else if (hours >= 12) {
            return "12시간 이내";
        } else if (hours >= 6) {
            return "6시간 이내";
        } else if (hours >= 3) {
            return "3시간 이내";
        } else if (hours >= 1) {
            return "1시간 이내";
        } else {
            return minutes + "분 후";
        }
    }
}