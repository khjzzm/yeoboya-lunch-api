package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.board.base.repository.reply.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractReplyService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.NoticeFetcher;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NoticeReplyService extends AbstractReplyService<Notice> {

    public NoticeReplyService(ReplyRepository replyRepository,
                              MemberRepository memberRepository,
                              NoticeFetcher noticeFetcher,
                              Response response) {
        super(replyRepository, memberRepository, noticeFetcher, response);
    }
}