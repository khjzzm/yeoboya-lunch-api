package com.yeoboya.lunch.api.v1.support.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.base.domain.QLike;
import com.yeoboya.lunch.api.v1.board.base.domain.QReply;
import com.yeoboya.lunch.api.v1.support.domain.QNotice;
import com.yeoboya.lunch.api.v1.support.domain.QNoticeFile;
import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeProjection> searchNotices(NoticeSearchCondition condition, Pageable pageable) {
        QNotice notice = QNotice.notice;
        QNoticeFile noticeFile = QNoticeFile.noticeFile;
        QReply reply = QReply.reply;
        QLike like = QLike.like;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(condition.getKeyword())) {
            switch (condition.getSearchType()) {
                case TITLE:
                    builder.and(notice.title.containsIgnoreCase(condition.getKeyword()));
                    break;
                case CONTENT:
                    builder.and(notice.content.containsIgnoreCase(condition.getKeyword()));
                    break;
                case TITLE_CONTENT:
                    builder.and(notice.title.containsIgnoreCase(condition.getKeyword())
                            .or(notice.content.containsIgnoreCase(condition.getKeyword())));
                    break;
                case AUTHOR:
                    builder.and(notice.author.containsIgnoreCase(condition.getKeyword()));
                    break;
                case COMMENT:
                    builder.and(JPAExpressions
                            .selectOne()
                            .from(reply)
                            .where(
                                    reply.board.id.eq(notice.id)
                                            .and(reply.content.containsIgnoreCase(condition.getKeyword()))
                            )
                            .exists());
                    break;
            }
        }

        List<NoticeProjection> results = queryFactory
                .select(Projections.constructor(NoticeProjection.class,
                        notice.id, notice.title, notice.content,
                        notice.category, notice.author, notice.pinned,
                        notice.startDate, notice.endDate,
                        notice.createdDate, notice.viewCount, notice.status,
                        JPAExpressions.select(like.count())
                                .from(like)
                                .where(like.board.id.eq(notice.id)),
                        JPAExpressions.select(reply.count())
                                .from(reply)
                                .where(reply.board.id.eq(notice.id)),
                        JPAExpressions
                                .selectOne()
                                .from(noticeFile)
                                .where(noticeFile.notice.id.eq(notice.id))
                                .exists()
                ))
                .from(notice)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        notice.pinned.desc(),
                        notice.createdDate.desc()
                )
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .from(notice)
                .where(builder);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

}