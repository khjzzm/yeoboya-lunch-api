package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FreeBoardDetailResponse {
    private Long boardId;
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private boolean hasLiked;
    private int pinned;
    private boolean secret;
    private List<HashTagResponse> hashTag;
    private final LocalDateTime createdDate;

    public static FreeBoardDetailResponse from(FreeBoard freeBoard) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory(),
                freeBoard.getViewCount(),
                false,
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getCreatedDate()
        );
    }

    public static FreeBoardDetailResponse from(FreeBoard freeBoard, boolean hasLiked) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory(),
                freeBoard.getViewCount(),
                hasLiked,
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getCreatedDate()
        );
    }

}
