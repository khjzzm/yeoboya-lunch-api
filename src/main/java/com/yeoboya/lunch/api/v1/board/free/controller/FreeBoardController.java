package com.yeoboya.lunch.api.v1.board.free.controller;

import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.response.CategoryResponse;
import com.yeoboya.lunch.api.v1.board.free.request.FreeBoardCreate;
import com.yeoboya.lunch.api.v1.board.free.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardDetailResponse;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardService;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.config.annotation.RateLimited;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class FreeBoardController {

    private final FreeBoardService freeBoardService;
    private final Response response;

    // 게시글 작성
    @RateLimited(limit = 1)
    @PostMapping("/free")
    public ResponseEntity<Response.Body> create(@Valid @RequestBody FreeBoardCreate freeBoardCreate) {
        FreeBoardDetailResponse freeBoard = freeBoardService.createFreeBoard(freeBoardCreate);
        return response.success(Code.SAVE_SUCCESS, freeBoard);
    }

    // 게시글 작성 (파일첨부)
    @PostMapping(value = "/free/image")
    public ResponseEntity<Response.Body> uploadPhoto(@RequestPart MultipartFile file) {
        FileResponse fileResponse = freeBoardService.uploadImage(file);
        return response.success(Code.SAVE_SUCCESS, fileResponse);
    }

    // 조회수 업데이트
    @PostMapping("/read")
    public ResponseEntity<Response.Body> updateViewCount(@RequestParam Long boardNo, @RequestParam String loginId) {
        freeBoardService.updateViewCount(boardNo, loginId);
        return response.success(Code.SEARCH_SUCCESS);
    }

    // 게시글 리스트 조회
    @GetMapping("/free")
    public ResponseEntity<Response.Body> getAllFreeBoards(@ModelAttribute BoardSearchCondition condition,
                                                          Pageable pageable){
        Map<String, Object> getAllFreeBoards = freeBoardService.getAllFreeBoards(condition, pageable);
        return response.success(Code.SEARCH_SUCCESS, getAllFreeBoards);
    }

    // 게시글 단건 조회
    @GetMapping("/free/detail")
    public ResponseEntity<Response.Body> getFreeBoardDetail(@RequestParam Long boardNo) {
        FreeBoardDetailResponse freeBoardDetailResponse = freeBoardService.getFreeBoardDetail(boardNo);
        return response.success(Code.SEARCH_SUCCESS, freeBoardDetailResponse);
    }

    // 게시글 수정
    @PutMapping("/free")
    public ResponseEntity<Response.Body> edit(@RequestParam Long boardNo,
                                              @RequestBody BoardEdit boardEdit) {
        FreeBoardDetailResponse freeBoardDetailResponse = freeBoardService.editBoard(boardNo, boardEdit);
        return response.success(Code.SEARCH_SUCCESS, freeBoardDetailResponse);
    }

    // 게시글 삭제
    @DeleteMapping("/free")
    public ResponseEntity<Response.Body> deleteNotice(@RequestParam Long boardNo) {
        freeBoardService.deleteFreeBoard(boardNo);
        return response.success(Code.DELETE_SUCCESS);
    }

    // 댓글 작성
    @PostMapping("/free/reply")
    public ResponseEntity<Response.Body> createReply(@Valid @RequestBody ReplyCreateRequest replyCreateRequest) {
        return freeBoardService.createReply(replyCreateRequest);
    }

    // 댓글 조회
    @GetMapping("/free/replies")
    public ResponseEntity<Response.Body> getReplies(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        return freeBoardService.fetchBoardReplies(boardSearchCondition, pageable);
    }

    // 댓글 삭제
    @DeleteMapping("/free/reply")
    public ResponseEntity<Response.Body> deleteReply(@RequestParam Long replyId) {
        return freeBoardService.deleteReply(replyId);
    }

    // 좋아요
    @PostMapping("/free/like")
    public ResponseEntity<Response.Body> like(@RequestParam Long boardNo) {
        return freeBoardService.likePost(boardNo);
    }

    // 좋아요 취소
    @DeleteMapping("/free/unlike")
    public ResponseEntity<Response.Body> unlike(@RequestParam Long boardNo) {
        return freeBoardService.unlikePost(boardNo);
    }
}