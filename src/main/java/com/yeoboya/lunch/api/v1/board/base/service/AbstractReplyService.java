package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.board.base.repository.reply.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearch;
import com.yeoboya.lunch.api.v1.board.free.response.ReplyResponse;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractReplyService<T extends AbstractBoard> {

    protected final ReplyRepository replyRepository;
    protected final MemberRepository memberRepository;
    protected final BoardFetcher<T> boardFetcher;
    protected final Response response;

    @Transactional
    public ResponseEntity<Response.Body> createReply(ReplyCreateRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found - " + request.getLoginId()));

        T board = boardFetcher.findById(request.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found - " + request.getBoardId()));

        Reply parentReply = null;
        if (request.getParentReplyId() != null) {
            parentReply = replyRepository.findById(request.getParentReplyId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent reply not found - " + request.getParentReplyId()));
        }

        Reply reply = Reply.createComment(member, board, request.getContent(), parentReply);
        replyRepository.save(reply);

        return response.success(Code.SAVE_SUCCESS);
    }

    public ResponseEntity<Response.Body> fetchBoardReplies(BoardSearch boardSearch, Pageable pageable) {
        Page<Reply> pagedReplies = replyRepository.getReplyForBoard(boardSearch, pageable);

        List<Reply> allReplies = pagedReplies.getContent();
        List<Reply> parentReplies = allReplies.stream().filter(r -> r.getParentReply() == null).collect(Collectors.toList());
        List<Reply> childReplies = allReplies.stream().filter(r -> r.getParentReply() != null).collect(Collectors.toList());

        List<ReplyResponse> replyResponses = parentReplies.stream()
                .map(parent -> ReplyResponse.of(parent.getMember(), parent, childReplies))
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(
                pagedReplies.getNumber() + 1,
                pagedReplies.isFirst(),
                pagedReplies.isLast(),
                pagedReplies.isEmpty(),
                pagedReplies.getTotalPages(),
                pagedReplies.getTotalElements()
        );

        Map<String, Object> responseData = Map.of(
                "list", replyResponses,
                "pagination", pagination
        );

        return response.success(Code.SEARCH_SUCCESS, responseData);
    }
}