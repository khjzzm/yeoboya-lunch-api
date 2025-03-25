package com.yeoboya.lunch.api.v1.board.base.repository.reply;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.api.v1.board.base.domain.QReply.reply;


@Repository
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom {

    private final JPAQueryFactory query;

    public ReplyRepositoryCustomImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Reply> getReplyForBoard(BoardSearch boardSearch, Pageable pageable) {
        List<Reply> content = query.selectFrom(reply)
                .where(reply.board.id.eq(boardSearch.getBoardId()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .distinct()
                .fetch();

        Long totalCount = query
                .select(reply.count())
                .from(reply)
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<Reply> getChildrenForReply(BoardSearch boardSearch, Pageable pageable) {
        List<Reply> content = query.selectFrom(reply)
                .where(reply.parentReply.id.eq(boardSearch.getParentReplyId()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .distinct()
                .fetch();

        Long totalCount = query
                .select(reply.count())
                .from(reply)
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }


}
