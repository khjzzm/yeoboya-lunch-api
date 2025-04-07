package com.yeoboya.lunch.api.v1.board.base.controller;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.request.CategoryCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.request.CategoryEditRequest;
import com.yeoboya.lunch.api.v1.board.base.response.CategoryResponse;
import com.yeoboya.lunch.api.v1.board.base.service.CategoryService;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final Response response;

    @GetMapping("/board/categories")
    public ResponseEntity<Response.Body> getCategoriesByBoardType(@RequestParam BoardType boardType) {
        List<CategoryResponse> categoriesByBoardType = categoryService.getCategoriesByBoardType(boardType);
        return response.success(Code.SEARCH_SUCCESS, categoriesByBoardType);
    }

    @PostMapping("/board/categories")
    public ResponseEntity<Response.Body> create(@RequestBody @Valid CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return response.success(Code.SAVE_SUCCESS);
    }

    @PutMapping("/board/categories")
    public ResponseEntity<Response.Body> update(@RequestParam Long id,
                                                @RequestBody @Valid CategoryEditRequest request) {
        categoryService.updateCategory(id, request);
        return response.success(Code.UPDATE_SUCCESS);
    }

    @DeleteMapping("/board/categories")
    public ResponseEntity<Response.Body> delete(@RequestParam Long id) {
        categoryService.deleteCategory(id);
        return response.success(Code.DELETE_SUCCESS);
    }
}
