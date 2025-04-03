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
    private boolean secret;
    private String loginId;
    private String name;
    private List<HashTagResponse> hashTag;
    private long replyCount;
    private long likeCount;
    private boolean like;

    public static FreeBoardResponse from(FreeBoard freeBoard) {

        return new FreeBoardResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.isSecret(),
                freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
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
                freeBoard.isSecret(),
                freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getReplies().size(),
                freeBoard.getLikes().size(),
                hasLiked
        );
    }
}
