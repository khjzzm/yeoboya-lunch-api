package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardTypeCategory;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardTypeCategoryRepository extends JpaRepository<BoardTypeCategory, Long> {
    void deleteByCategoryId(Long categoryId);
    boolean existsByBoardTypeAndCategory(BoardType boardType, Category category);
}
