package com.yeoboya.lunch.api.v1.board.base.repository.reply;

import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRepositoryCustom {

    Page<Reply> getReplyForBoard(BoardSearch boardSearch, Pageable pageable);

    Page<Reply> getChildrenForReply(BoardSearch boardSearch, Pageable pageable);
}
