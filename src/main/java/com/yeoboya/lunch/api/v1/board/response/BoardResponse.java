package com.yeoboya.lunch.api.v1.board.response;

import com.yeoboya.lunch.api.v1.board.domain.Board;
import com.yeoboya.lunch.api.v1.board.domain.Reply;
import com.yeoboya.lunch.api.v1.file.domain.BoardFile;
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

    public static BoardResponse from(Board board) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a HH:mm");

        List<Reply> parentReplies = board.getReplies().stream()
                .filter(reply -> reply.getParentReply() == null)
                .collect(Collectors.toList());

        BoardResponse response = new BoardResponse(
                board.getId(), board.getTitle(), board.getContent(), board.isSecret(), board.getMember().getLoginId(),
                board.getMember().getName(), simpleDateFormat.format(board.getCreateDate()),
                board.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                board.getBoardFiles().stream().map(BoardFileResponse::from).collect(Collectors.toList()),
                parentReplies.stream().map(r -> ReplyResponse.of(r.getMember(), r, r.getBoard().getReplies())).collect(Collectors.toList()),
                board.getReplies().size(),
                board.getLikes().size()
        );
        return response;
    }

    public static BoardResponse from(Board board, Page<Reply> replies, boolean liked) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a HH:mm");

        List<Reply> allReplies = replies.getContent();
        List<Reply> parentReplies = allReplies.stream()
                .filter(reply -> reply.getParentReply() == null)
                .collect(Collectors.toList());

        BoardResponse response = new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.isSecret(),
                board.getMember().getLoginId(),
                board.getMember().getName(),
                simpleDateFormat.format(board.getCreateDate()),
                board.getBoardHashTag().stream().map(r -> HashTagResponse.from(r.getHashTag())).collect(Collectors.toList()),
                board.getBoardFiles().stream().map(BoardFileResponse::from).collect(Collectors.toList()),
                parentReplies.stream().map(r -> ReplyResponse.of(r.getMember(), r, allReplies)).collect(Collectors.toList()),
                board.getReplies().size(),
                board.getLikes().size()
        );
        response.setClickLiked(liked);
        return response;
    }
}
