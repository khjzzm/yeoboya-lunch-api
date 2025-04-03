package com.yeoboya.lunch.api.v1.board.free.repository;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.api.v1.board.base.domain.QBoardHashTag.boardHashTag;
import static com.yeoboya.lunch.api.v1.board.base.domain.QHashTag.hashTag;
import static com.yeoboya.lunch.api.v1.board.free.domain.QFreeBoard.freeBoard;
import static com.yeoboya.lunch.api.v1.member.domain.QMember.member;


@Repository
public class FreeBoardRepositoryCustomImpl implements FreeBoardRepositoryCustom {

    private final JPAQueryFactory query;

    public FreeBoardRepositoryCustomImpl(JPAQueryFactory query) {
        this.query = query;
    }

    /**
     * Retrieves a page of boards based on the given search criteria and pageable parameters.
     *
     * @param boardSearchCondition - the search criteria to apply
     * @param pageable - the pageable parameters for pagination
     * @return a page of boards
     * 1:N 관계에선 fetchJoin()을 신중히 써야 합니다.
     * 댓글처럼 데이터가 많아질 가능성이 있는 엔티티는 지연 로딩이나 별도 조회로 분리하는 것이 안전합니다.
     */
    @Override
    public Page<FreeBoard> boardList(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        // 메인 콘텐츠 조회 쿼리 (N+1 방지용 fetchJoin 사용)
        List<FreeBoard> content = query.selectFrom(freeBoard)
                .leftJoin(freeBoard.member, member).fetchJoin()
                .leftJoin(freeBoard.boardHashTag, boardHashTag).fetchJoin()
                .leftJoin(boardHashTag.hashTag, hashTag).fetchJoin()
//                .leftJoin(freeBoard.freeBoardFiles, freeBoardFile).fetchJoin()
//                .leftJoin(freeBoard.replies, reply) // fetchJoin 제외, 너무 많은 데이터일 경우
                .where( /* 조건절 필요시 */ )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(freeBoard.id.desc())
                .distinct()
                .fetch();

        // 카운트 쿼리 (fetchJoin 사용 X, 최적화된 count 전용)
        JPAQuery<Long> countQuery = query
                .select(freeBoard.countDistinct())
                .from(freeBoard)
                .leftJoin(freeBoard.boardHashTag, boardHashTag)
                .leftJoin(boardHashTag.hashTag, hashTag)
                .leftJoin(freeBoard.member, member);

        // PageableExecutionUtils.getPage 메소드를 사용하여 필요할 때만 카운트 쿼리를 실행합니다.
        // 이 방법은 콘텐츠 리스트가 페이지 사이즈에 도달하지 않았거나, 마지막 페이지인 경우 총 개수 쿼리를 실행하지 않도록 합니다.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
