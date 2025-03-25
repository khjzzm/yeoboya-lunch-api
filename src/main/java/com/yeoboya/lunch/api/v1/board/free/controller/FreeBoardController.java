package com.yeoboya.lunch.api.v1.board.free.controller;

import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.free.request.BoardCreate;
import com.yeoboya.lunch.api.v1.board.free.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearch;
import com.yeoboya.lunch.api.v1.board.free.request.FileBoardCreate;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardLikeService;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardReplyService;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardService;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.config.annotation.RateLimited;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.awt.print.Pageable;
import java.security.Principal;

@RestController
@RequestMapping("/free")
@RequiredArgsConstructor
@Slf4j
public class FreeBoardController {

    private final FreeBoardService freeBoardService;
    private final FileServiceS3 fileServiceS3;

    private final FreeBoardReplyService replyService;
    private final FreeBoardLikeService likeService;

    private final Response response;

    // 게시글 작성
    @RateLimited(limit = 1)
    @PostMapping("/write")
    public ResponseEntity<Response.Body> create(@RequestBody @Valid BoardCreate boardCreate) {
        return freeBoardService.saveBoard(boardCreate);
    }

    // 게시글 작성 (파일첨부)
    @PostMapping(value = "/write/photo", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<Response.Body> createPhoto(@RequestPart MultipartFile file, @RequestPart @Valid FileBoardCreate fileBoardCreate) {
        return freeBoardService.saveBoardPhoto(file, fileBoardCreate);
    }

    // 게시글 리스트 조회
    @GetMapping
    public ResponseEntity<Response.Body> list(BoardSearch boardSearch, Pageable pageable) {
        return freeBoardService.list(boardSearch, pageable);
    }

    // 게시글 단건 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<Response.Body> findBoardById(@PathVariable Long boardId, Pageable pageable) {
        return freeBoardService.findBoardById(boardId, pageable);
    }

    // 게시글 수정
    @PatchMapping("/edit")
    public ResponseEntity<Response.Body> edit(@RequestBody BoardEdit boardEdit, Principal principal) {
        return freeBoardService.editBoard(boardEdit, principal);
    }

    // 댓글 작성
    @PostMapping("/reply")
    public ResponseEntity<Response.Body> createReply(@Valid @RequestBody ReplyCreateRequest replyCreateRequest) {
        return replyService.createReply(replyCreateRequest);
    }

    // 댓글 조회
    @GetMapping("/replies")
    public ResponseEntity<Response.Body> getReplies(BoardSearch boardSearch, Pageable pageable) {
        return replyService.fetchBoardReplies(boardSearch, pageable);
    }

    // 좋아요
    @PostMapping("/like")
    public ResponseEntity<Response.Body> like(@RequestParam Long boardId) {
        return likeService.likePost(boardId);
    }

    // 좋아요 취소
    @DeleteMapping("/unlike")
    public ResponseEntity<Response.Body> unlike(@RequestParam Long boardId) {
        return likeService.unlikePost(boardId);
    }
}