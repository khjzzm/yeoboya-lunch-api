package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FreeBoardDetailResponse {
    private Long boardNo;
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private boolean hasLiked;
    private boolean secret;
    private List<HashTagResponse> hashTag;
    private final LocalDateTime createdDate;
    private final MemberResponse member;
    private boolean mine;

    public static FreeBoardDetailResponse restricted(FreeBoard freeBoard) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                null, // 내용 숨김
                freeBoard.getCategory().getName(),
                freeBoard.getViewCount(),
                false,
                true,
                List.of(), // 해시태그 숨김
                freeBoard.getCreatedDate(),
                null, // 작성자 숨김
                false
        );
    }

    public static FreeBoardDetailResponse from(FreeBoard freeBoard) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory().getName(),
                freeBoard.getViewCount(),
                false,
                freeBoard.isSecret(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getCreatedDate(),
                MemberResponse.from(freeBoard.getMember()),
                false
        );
    }

    public static FreeBoardDetailResponse from(FreeBoard freeBoard, boolean hasLiked, boolean mine) {
        return new FreeBoardDetailResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.getCategory().getName(),
                freeBoard.getViewCount(),
                hasLiked,
                freeBoard.isSecret(),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getCreatedDate(),
                MemberResponse.from(freeBoard.getMember()),
                mine
        );
    }

}
