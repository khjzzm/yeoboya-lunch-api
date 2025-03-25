package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.board.base.repository.like.LikeRepository;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class AbstractLikeService<T extends AbstractBoard> {

    protected final LikeRepository likeRepository;
    protected final MemberRepository memberRepository;
    protected final BoardFetcher<T> boardFetcher;
    protected final Response response;

    @Transactional
    public ResponseEntity<Response.Body> likePost(Long postId) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음: " + loginId));

        T post = boardFetcher.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음: " + postId));

        if (likeRepository.existsByMemberAndBoard(member, post)) {
            return response.fail(ErrorCode.DUPLICATE_RESOURCE, "이미 좋아요 누름");
        }

        Like like = Like.createLike(member, post);
        likeRepository.save(like);
        post.addLike(like);

        return response.success(Code.SAVE_SUCCESS);
    }

    @Transactional
    public ResponseEntity<Response.Body> unlikePost(Long postId) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

        T post = boardFetcher.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음: " + postId));

        Like like = likeRepository.findByMemberLoginIdAndBoardId(loginId, postId)
                .orElseThrow(() -> new EntityNotFoundException("좋아요 없음"));

        post.removeLike(like);
        likeRepository.delete(like);
        return response.success(Code.SAVE_SUCCESS);
    }

    @Transactional
    public boolean hasLiked(String loginId, Long postId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음: " + loginId));

        return likeRepository.findByMemberLoginIdAndBoardId(member.getLoginId(), postId).isPresent();
    }
}