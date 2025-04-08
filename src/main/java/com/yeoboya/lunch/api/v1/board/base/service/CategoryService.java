package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardTypeCategory;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import com.yeoboya.lunch.api.v1.board.base.repository.category.BoardTypeCategoryRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.category.CategoryRepository;
import com.yeoboya.lunch.api.v1.board.base.request.CategoryCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.request.CategoryEditRequest;
import com.yeoboya.lunch.api.v1.board.base.response.CategoryResponse;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BoardTypeCategoryRepository boardTypeCategoryRepository;

    /**
     * ID로 단일 카테고리 조회
     */
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID=" + categoryId));
    }

    /**
     * 게시판 타입별 카테고리 목록 조회
     */
    public List<CategoryResponse> getCategoriesByBoardType(BoardType boardType) {
        return categoryRepository.findByBoardType(boardType).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 카테고리 목록 조회
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    @Transactional
    public void createCategory(CategoryCreateRequest request) {
        // 동일 이름 카테고리 존재 여부 확인
        Category category = categoryRepository.findByName(request.getName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(request.getName());
                    newCategory.setDescription(request.getDescription());
                    return categoryRepository.save(newCategory);
                });

        // 중복 매핑 방지 (이미 존재할 경우 무시)
        boolean exists = boardTypeCategoryRepository.existsByBoardTypeAndCategory(request.getBoardType(), category);
        if (!exists) {
            BoardTypeCategory mapping = BoardTypeCategory.of(request.getBoardType(), category);
            boardTypeCategoryRepository.save(mapping);
        }
    }

    @Transactional
    public void updateCategory(Long categoryId, CategoryEditRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리 없음"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        boardTypeCategoryRepository.deleteByCategoryId(categoryId);
    }


}