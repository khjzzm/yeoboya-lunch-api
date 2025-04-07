package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    // 이름으로 카테고리 찾기
    Category findByName(String name);

    // 여러 이름으로 찾기
    List<Category> findByNameIn(List<String> names);

    // 사용 여부, 게시판 유형 등에 따라 필터링하고 싶다면 커스텀 추가 가능
}