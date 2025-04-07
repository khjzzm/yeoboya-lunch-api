package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardTypeCategoryRepository extends JpaRepository<BoardTypeCategory, Long> {
    List<BoardTypeCategory> findByBoardType(BoardType boardType);
    void deleteByCategoryId(Long categoryId);
}
