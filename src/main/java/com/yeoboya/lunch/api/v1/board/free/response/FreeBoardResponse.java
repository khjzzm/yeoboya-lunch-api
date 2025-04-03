package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class FreeBoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private String summary;
    private String category;
    private boolean secret;
    private String loginId;
    private String name;
    private int viewCount;
    private long replyCount;
    private long likeCount;
    private boolean like;

    public static FreeBoardResponse from(FreeBoard freeBoard) {

        return new FreeBoardResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                createSummary(freeBoard.getContent()),
                freeBoard.getCategory(),
                freeBoard.isSecret(),
                freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(),
                freeBoard.getViewCount(),
                freeBoard.getReplies().size(),
                freeBoard.getLikes().size(),
                false
        );
    }


    public static FreeBoardResponse from(FreeBoard freeBoard, boolean hasLiked) {
        return new FreeBoardResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                createSummary(freeBoard.getContent()),
                freeBoard.getCategory(),
                freeBoard.isSecret(),
                freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(),
                freeBoard.getViewCount(),
                freeBoard.getReplies().size(),
                freeBoard.getLikes().size(),
                hasLiked
        );
    }

    private static String createSummary(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
