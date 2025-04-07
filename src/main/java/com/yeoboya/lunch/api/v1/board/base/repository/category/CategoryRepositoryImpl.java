package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import com.yeoboya.lunch.api.v1.board.base.domain.QBoardTypeCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findByBoardType(BoardType boardType) {
        QBoardTypeCategory btc = QBoardTypeCategory.boardTypeCategory;

        return queryFactory
                .select(btc.category)
                .from(btc)
                .where(btc.boardType.eq(boardType))
                .fetch();
    }
}