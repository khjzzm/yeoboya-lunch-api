package com.yeoboya.lunch.api.v1.board.controller.specification;

import com.yeoboya.lunch.api.v1.board.request.BoardCreate;
import com.yeoboya.lunch.api.v1.board.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.request.BoardSearch;
import com.yeoboya.lunch.api.v1.board.request.FileBoardCreate;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@Tag(name = "Board", description = "게시판 관련 API")
public interface BoardApi {

    //@PreAuthorize("hasRole('USER')")
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping("/write")
    ResponseEntity<Body> create(@RequestBody @Valid BoardCreate boardCreate);

    @Operation(summary = "게시글 작성 (파일 첨부)", description = "파일을 첨부하여 게시글을 작성합니다.")
    @PostMapping(value = "/write/photo", consumes = {"multipart/form-data", "application/json"})
    ResponseEntity<Body> createPhoto(@RequestPart MultipartFile file, @RequestPart @Valid FileBoardCreate fileBoardCreate);

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @GetMapping
    ResponseEntity<Body> list(BoardSearch boardSearch, Pageable pageable);

    @Operation(summary = "게시글 단건 조회", description = "특정 게시글을 조회합니다.")
    @GetMapping("/{boardId}")
    ResponseEntity<Body> findBoardById(@PathVariable Long boardId, Pageable pageable);

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PatchMapping("/edit")
    ResponseEntity<Body> edit(@RequestBody BoardEdit boardEdit, Principal principal);
}