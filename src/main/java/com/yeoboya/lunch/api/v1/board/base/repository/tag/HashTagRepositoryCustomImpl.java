package com.yeoboya.lunch.api.v1.board.base.repository.tag;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.base.domain.QBoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.QHashTag;
import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashTagRepositoryCustomImpl implements HashTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QHashTag hashTag = QHashTag.hashTag;
    QBoardHashTag boardHashTag = QBoardHashTag.boardHashTag;

    @Override
    public List<HashTagResponse> findTopHashtags(String keyword, int limit) {
        return queryFactory
                .select(Projections.constructor(HashTagResponse.class,
                        hashTag.tag,
                        boardHashTag.count()
                ))
                .from(boardHashTag)
                .join(boardHashTag.hashTag, hashTag)
                .where(hashTag.tag.startsWithIgnoreCase(keyword))
                .groupBy(hashTag.tag)
                .orderBy(boardHashTag.count().desc())
                .limit(limit)
                .fetch();
    }
}