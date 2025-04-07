package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findByBoardType(BoardType boardType);
}