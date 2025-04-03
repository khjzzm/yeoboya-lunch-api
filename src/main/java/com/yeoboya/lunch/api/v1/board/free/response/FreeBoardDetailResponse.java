package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import com.yeoboya.lunch.api.v1.support.response.NoticeDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreeBoardDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String category;
    private int pinned;
    private boolean secret;
    private int viewCount;
    private boolean hasLiked;

    public static FreeBoardDetailResponse from(FreeBoard freeBoard) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                createSummary(freeBoard.getContent()),
                freeBoard.getCategory(),
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getViewCount(),
                false
        );
    }

    public static FreeBoardDetailResponse from(FreeBoard freeBoard, boolean hasLiked) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                createSummary(freeBoard.getContent()),
                freeBoard.getCategory(),
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getViewCount(),
                hasLiked
        );
    }


    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
