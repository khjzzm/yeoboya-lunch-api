package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.board.base.repository.like.LikeRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractLikeService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.NoticeFetcher;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import org.springframework.stereotype.Service;

@Service
public class NoticeLikeService extends AbstractLikeService<Notice> {

    public NoticeLikeService(LikeRepository likeRepository,
                             MemberRepository memberRepository,
                             NoticeFetcher noticeFetcher,
                             Response response) {
        super(likeRepository, memberRepository, noticeFetcher, response);
    }
}