package com.yeoboya.lunch.api.v1.board.base.repository.reply;

import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRepositoryCustom {

    Page<Reply> getReplyForBoard(BoardSearchCondition boardSearchCondition, Pageable pageable);

    Page<Reply> getChildrenForReply(BoardSearchCondition boardSearchCondition, Pageable pageable);
}
