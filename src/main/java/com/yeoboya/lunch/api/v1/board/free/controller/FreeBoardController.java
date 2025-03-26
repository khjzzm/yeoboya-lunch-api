package com.yeoboya.lunch.api.v1.board.free.controller;

import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.free.request.BoardCreate;
import com.yeoboya.lunch.api.v1.board.free.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardLikeService;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardReplyService;
import com.yeoboya.lunch.api.v1.board.free.service.FreeBoardService;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.BoardFileResponse;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.config.annotation.RateLimited;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.function.Function;

@RestController
@RequestMapping("/board")
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
    @PostMapping("/free")
    public ResponseEntity<Response.Body> create(@Valid @RequestBody BoardCreate boardCreate) {
        return freeBoardService.createFreeBoard(boardCreate);
    }

    // 게시글 작성 (파일첨부)
    @PostMapping(value = "/free/image", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<Response.Body> createPhoto(@RequestPart MultipartFile file) {
        Function<FileResponse, BoardFileResponse> responseMapper = BoardFileResponse::apply;
        FileResponse fileResponse = fileServiceS3.upload(file, Directory.FREE_BOARD, responseMapper);
        return response.success(Code.SAVE_SUCCESS, fileResponse);

    }

    // 조회수 업데이트
    @PostMapping("/read")
    public ResponseEntity<Response.Body> updateViewCount(@RequestParam Long boardId, @RequestParam String loginId) {
        freeBoardService.updateViewCount(boardId, loginId);
        return response.success(Code.SEARCH_SUCCESS);
    }

    // 게시글 리스트 조회
    @GetMapping("/free")
    public ResponseEntity<Response.Body> list(@ModelAttribute BoardSearchCondition condition,
                                              Pageable pageable){
        return freeBoardService.list(condition, pageable);
    }

    // 게시글 단건 조회
    @GetMapping("/free/detail")
    public ResponseEntity<Response.Body> findBoardById(@RequestParam Long boardId) {
        return freeBoardService.findBoardById(boardId);
    }

    // 게시글 수정
    @PutMapping("/free")
    public ResponseEntity<Response.Body> edit(@RequestBody BoardEdit boardEdit) {
        return freeBoardService.editBoard(boardEdit);
    }

    // 게시글 삭제
    @DeleteMapping("/free")
    public ResponseEntity<Response.Body> deleteNotice(@RequestParam Long boardId) {
        freeBoardService.deleteFreeBoard(boardId);
        return response.success(Code.DELETE_SUCCESS);
    }

    // 댓글 작성
    @PostMapping("/free/reply")
    public ResponseEntity<Response.Body> createReply(@Valid @RequestBody ReplyCreateRequest replyCreateRequest) {
        return replyService.createReply(replyCreateRequest);
    }

    // 댓글 조회
    @GetMapping("/free/replies")
    public ResponseEntity<Response.Body> getReplies(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        return replyService.fetchBoardReplies(boardSearchCondition, pageable);
    }

    // 좋아요
    @PostMapping("/free//like")
    public ResponseEntity<Response.Body> like(@RequestParam Long boardId) {
        return likeService.likePost(boardId);
    }

    // 좋아요 취소
    @DeleteMapping("/free/unlike")
    public ResponseEntity<Response.Body> unlike(@RequestParam Long boardId) {
        return likeService.unlikePost(boardId);
    }
}