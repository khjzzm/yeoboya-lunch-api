package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FreeBoardDetailResponse {
    private Long boardId;
    private String title;
    private String content;
    private String category;
    private int pinned;
    private boolean secret;
    private int viewCount;
    private List<HashTagResponse> hashTag;
    private boolean hasLiked;

    public static FreeBoardDetailResponse from(FreeBoard freeBoard) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory(),
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getViewCount(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                false
        );
    }

    public static FreeBoardDetailResponse from(FreeBoard freeBoard, boolean hasLiked) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory(),
                freeBoard.getPin(),
                freeBoard.isSecret(),
                freeBoard.getViewCount(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                hasLiked
        );
    }



}
