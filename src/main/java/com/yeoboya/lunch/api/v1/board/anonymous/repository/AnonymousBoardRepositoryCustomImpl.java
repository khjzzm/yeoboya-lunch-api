package com.yeoboya.lunch.api.v1.board.anonymous.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.board.anonymous.domain.QAnonymousBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnonymousBoardRepositoryCustomImpl implements AnonymousBoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QAnonymousBoard anonymousBoard = QAnonymousBoard.anonymousBoard;

    @Override
    public Slice<AnonymousBoard> findAllBySlice(Pageable pageable) {
        List<AnonymousBoard> content = queryFactory
                .selectFrom(anonymousBoard)
                .where(
                        anonymousBoard.deleteAt.isNull()
                                .or(anonymousBoard.deleteAt.gt(OffsetDateTime.now(ZoneOffset.ofHours(9))))
                )
                .orderBy(anonymousBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 여부 확인용
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(content.size() - 1);

        return new SliceImpl<>(content, pageable, hasNext);
    }
}