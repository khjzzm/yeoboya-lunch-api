package com.yeoboya.lunch.api.v1.board.free.response;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.file.response.BoardFileResponse;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class BoardResponse {

    private final Long boardId;
    private final String title;
    private final String content;
    private final boolean secret;
    private final String loginId;
    private final String name;
    private final String createDate;
    private final List<HashTagResponse> hashTag;
    private final List<FileResponse> files;
    private final List<ReplyResponse> replies;
    private final long replyCount;
    private final long likeCount;
    private boolean clickLiked;

    public static BoardResponse from(FreeBoard freeBoard) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a HH:mm");

        List<Reply> parentReplies = freeBoard.getReplies().stream()
                .filter(reply -> reply.getParentReply() == null)
                .collect(Collectors.toList());

        BoardResponse response = new BoardResponse(
                freeBoard.getId(), freeBoard.getTitle(), freeBoard.getContent(), freeBoard.isSecret(), freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(), simpleDateFormat.format(freeBoard.getCreateDate()),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getBoardFiles().stream().map(BoardFileResponse::from).collect(Collectors.toList()),
                parentReplies.stream().map(r -> ReplyResponse.of(r.getMember(), r, r.getBoard().getReplies())).collect(Collectors.toList()),
                freeBoard.getReplies().size(),
                freeBoard.getLikes().size()
        );
        return response;
    }

    public static BoardResponse from(FreeBoard freeBoard, Page<Reply> replies, boolean liked) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a HH:mm");

        List<Reply> allReplies = replies.getContent();
        List<Reply> parentReplies = allReplies.stream()
                .filter(reply -> reply.getParentReply() == null)
                .collect(Collectors.toList());

        BoardResponse response = new BoardResponse(
                freeBoard.getId(),
                freeBoard.getTitle(),
                freeBoard.getContent(),
                freeBoard.isSecret(),
                freeBoard.getMember().getLoginId(),
                freeBoard.getMember().getName(),
                simpleDateFormat.format(freeBoard.getCreateDate()),
                freeBoard.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                freeBoard.getBoardFiles().stream().map(BoardFileResponse::from).collect(Collectors.toList()),
                parentReplies.stream().map(r -> ReplyResponse.of(r.getMember(), r, allReplies)).collect(Collectors.toList()),
                freeBoard.getReplies().size(),
                freeBoard.getLikes().size()
        );
        response.setClickLiked(liked);
        return response;
    }
}
