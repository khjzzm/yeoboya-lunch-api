package com.yeoboya.lunch.api.v1.board.free.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardResponse;
import com.yeoboya.lunch.api.v1.board.free.response.QFreeBoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.api.v1.board.base.domain.QLike.like;
import static com.yeoboya.lunch.api.v1.board.base.domain.QReply.reply;
import static com.yeoboya.lunch.api.v1.board.free.domain.QFreeBoard.freeBoard;
import static com.yeoboya.lunch.api.v1.member.domain.QMember.member;


@Repository
public class FreeBoardRepositoryCustomImpl implements FreeBoardRepositoryCustom {

    private final JPAQueryFactory query;

    public FreeBoardRepositoryCustomImpl(JPAQueryFactory query) {
        this.query = query;
    }

    /**
     * 1:N 관계에선 fetchJoin()을 신중히 써야 합니다.
     * 댓글처럼 데이터가 많아질 가능성이 있는 엔티티는 지연 로딩이나 별도 조회로 분리하는 것이 안전합니다.
     */
    @Override
    public Page<FreeBoardResponse> boardList(BoardSearchCondition boardSearchCondition, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (boardSearchCondition.getKeyword() != null && boardSearchCondition.getSearchType() != null) {
            String keyword = boardSearchCondition.getKeyword();

            switch (boardSearchCondition.getSearchType()) {
                case TITLE:
                    builder.and(freeBoard.title.containsIgnoreCase(keyword));
                    break;
                case CONTENT:
                    builder.and(freeBoard.content.containsIgnoreCase(keyword));
                    break;
                case TITLE_CONTENT:
                    builder.and(
                            freeBoard.title.containsIgnoreCase(keyword)
                                    .or(freeBoard.content.containsIgnoreCase(keyword))
                    );
                    break;
                case AUTHOR:
                    builder.and(freeBoard.member.name.containsIgnoreCase(keyword)
                            .or(freeBoard.member.loginId.containsIgnoreCase(keyword)));
                    break;
                case COMMENT:
                    builder.and(freeBoard.replies.any().content.containsIgnoreCase(keyword));
                    break;
            }
        }

        // 메인 콘텐츠 조회 쿼리 (N+1 방지용 fetchJoin 사용)
        List<FreeBoardResponse> content = query
                .select(new QFreeBoardResponse(
                        freeBoard.id,
                        freeBoard.title,
                        freeBoard.content,
                        freeBoard.content.substring(0, 100), // summary
                        freeBoard.category,
                        freeBoard.secret,
                        member.loginId,
                        member.name,
                        freeBoard.viewCount,
                        JPAExpressions.select(like.count())
                                .from(like)
                                .where(like.board.id.eq(freeBoard.id)),
                        JPAExpressions.select(reply.count())
                                .from(reply)
                                .where(reply.board.id.eq(freeBoard.id)),
                        freeBoard.createdDate
                ))
                .from(freeBoard)
                .leftJoin(freeBoard.member, member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(freeBoard.count())
                .from(freeBoard)
                .where(builder);


        // PageableExecutionUtils.getPage 메소드를 사용하여 필요할 때만 카운트 쿼리를 실행합니다.
        // 이 방법은 콘텐츠 리스트가 페이지 사이즈에 도달하지 않았거나, 마지막 페이지인 경우 총 개수 쿼리를 실행하지 않도록 합니다.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
