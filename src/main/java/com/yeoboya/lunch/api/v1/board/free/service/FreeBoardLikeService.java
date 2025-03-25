package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.repository.like.LikeRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractLikeService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.FreeBoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class FreeBoardLikeService extends AbstractLikeService<FreeBoard> {

    public FreeBoardLikeService(LikeRepository likeRepository,
                                MemberRepository memberRepository,
                                FreeBoardFetcher boardFetcher,
                                Response response) {
        super(likeRepository, memberRepository, boardFetcher, response);
    }
}