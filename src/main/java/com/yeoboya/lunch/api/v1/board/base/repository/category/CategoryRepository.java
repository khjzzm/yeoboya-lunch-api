package com.yeoboya.lunch.api.v1.board.base.repository.category;

import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Optional<Category> findByName(String name);
}