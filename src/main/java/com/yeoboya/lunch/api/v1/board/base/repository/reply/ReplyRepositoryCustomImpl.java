package com.yeoboya.lunch.api.v1.board.base.repository.reply;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.api.v1.board.base.domain.QReply.reply;
import static com.yeoboya.lunch.api.v1.member.domain.QMember.member;


@Repository
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom {

    private final JPAQueryFactory query;

    public ReplyRepositoryCustomImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Reply> getReplyForBoard(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        List<Reply> content = query.selectFrom(reply)
                .join(reply.member, member).fetchJoin()
                .where(reply.board.id.eq(boardSearchCondition.getBoardId()))
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
    public Page<Reply> getChildrenForReply(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        List<Reply> content = query.selectFrom(reply)
                .where(reply.parentReply.id.eq(boardSearchCondition.getParentReplyId()))
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
