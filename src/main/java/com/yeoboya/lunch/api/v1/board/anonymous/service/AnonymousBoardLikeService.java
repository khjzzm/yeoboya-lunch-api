package com.yeoboya.lunch.api.v1.board.anonymous.service;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.board.base.repository.like.LikeRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractLikeService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.AnonymousBoardFetcher;
import com.yeoboya.lunch.api.v1.common.exception.BadRequestException;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.config.redis.RedisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class AnonymousBoardLikeService extends AbstractLikeService<AnonymousBoard> {

    private final RedisUtil redisUtil;

    public AnonymousBoardLikeService(LikeRepository likeRepository,
                                     MemberRepository memberRepository,
                                     AnonymousBoardFetcher boardFetcher,
                                     Response response, RedisUtil redisUtil) {
        super(likeRepository, memberRepository, boardFetcher, response);
        this.redisUtil = redisUtil;
    }


    @Transactional
    public ResponseEntity<Response.Body> likePost(Long boardId, String clientUUID) {
        AnonymousBoard post = boardFetcher.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("익명 게시글 없음: " + boardId));

        String redisKey = String.format("anonymous:like:%s:%d", clientUUID, boardId);

        // 하루에 한 번만 허용
        if (redisUtil.hasKey(redisKey)) {
            throw new BadRequestException("좋아요는 1일 1회만 가능합니다.");
        }

        // 좋아요 저장
        Like like = Like.createAnonymousLike(post);
        likeRepository.save(like);
        post.addLike(like);

        // Redis에 저장 (TTL: 하루)
        redisUtil.setStringOps(redisKey, "1", 1, TimeUnit.DAYS);

        return response.success(Code.SAVE_SUCCESS);
    }
}
