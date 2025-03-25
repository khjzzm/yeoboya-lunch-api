package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.repository.reply.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractReplyService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.FreeBoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class FreeBoardReplyService extends AbstractReplyService<FreeBoard> {

    public FreeBoardReplyService(ReplyRepository replyRepository,
                                  MemberRepository memberRepository,
                                  FreeBoardFetcher boardFetcher,
                                  Response response) {
        super(replyRepository, memberRepository, boardFetcher, response);
    }
}