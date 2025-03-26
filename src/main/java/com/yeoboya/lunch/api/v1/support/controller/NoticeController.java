package com.yeoboya.lunch.api.v1.support.controller;

import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.NoticeFileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeDetailResponse;
import com.yeoboya.lunch.api.v1.support.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final FileServiceS3 fileServiceS3;

    private final Response response;

    // 공지 작성
    @PostMapping("/notice")
    public ResponseEntity<Response.Body> createNotice(@Valid @RequestBody NoticeRequest noticeRequest) {
        Notice notice = noticeService.createNotice(noticeRequest);
        return response.success(Code.SAVE_SUCCESS, notice);
    }

    // 이미지 등록
    @PostMapping("/notice/image")
    public ResponseEntity<Response.Body> uploadImage(@RequestParam MultipartFile file) {
        Function<FileResponse, NoticeFileResponse> responseMapper = NoticeFileResponse::apply;
        FileResponse fileResponse = fileServiceS3.upload(file, Directory.NOTICE, responseMapper);
        return response.success(Code.SAVE_SUCCESS, fileResponse);
    }

    // 조회수 업데이트
    @PostMapping("/notice/read")
    public ResponseEntity<Response.Body> markNoticeAsRead(@RequestParam Long noticeId, @RequestParam String loginId) {
        noticeService.markNoticeAsRead(noticeId, loginId);
        return response.success(Code.SEARCH_SUCCESS);
    }

    // 전체 조회
    @GetMapping("/notice")
    public ResponseEntity<Response.Body> getAllBoardsWithReadStatus(
            @ModelAttribute NoticeSearchCondition condition,
            Pageable pageable
    ) {
        Map<String, Object> allNoticesWithReadStatus = noticeService.getAllNoticesWithReadStatus(condition, pageable);
        return response.success(Code.SEARCH_SUCCESS, allNoticesWithReadStatus);
    }

    // 단건 조회
    @GetMapping("/notice/detail")
    public ResponseEntity<Response.Body> getNoticeDetail(@RequestParam Long noticeId) {
        NoticeDetailResponse detail = noticeService.getNoticeDetail(noticeId);
        return response.success(Code.SEARCH_SUCCESS, detail);
    }

    // 공지 수정
    @PutMapping("/notice")
    public ResponseEntity<Response.Body> updateNotice(@RequestParam Long noticeId,
                                                      @Valid @RequestBody NoticeRequest noticeRequest) {
        Notice updated = noticeService.updateNotice(noticeId, noticeRequest);
        return response.success(Code.UPDATE_SUCCESS, updated);
    }

    // 공지 삭제
    @DeleteMapping("/notice")
    public ResponseEntity<Response.Body> deleteNotice(@RequestParam Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return response.success(Code.DELETE_SUCCESS);
    }

    // 댓글 작성
    @PostMapping("/notice/reply")
    public ResponseEntity<Response.Body> createReply(@Valid @RequestBody ReplyCreateRequest replyCreateRequest) {
        return noticeService.createReply(replyCreateRequest);
    }

    // 댓글 조회
    @GetMapping("/notice/replies")
    public ResponseEntity<Response.Body> getNoticeReplies(BoardSearchCondition search, Pageable pageable) {
        return noticeService.fetchBoardReplies(search, pageable);
    }

    // 댓글 삭제
    @DeleteMapping("/notice/reply")
    public ResponseEntity<Response.Body> deleteReply(@RequestParam Long replyId) {
        return noticeService.deleteReply(replyId);
    }

    // 좋아요
    @PostMapping("/notice/like")
    public ResponseEntity<Response.Body> likeNotice(@RequestParam Long noticeId) {
        return noticeService.likePost(noticeId);
    }

    // 좋아요 취소
    @DeleteMapping("/notice/unlike")
    public ResponseEntity<Response.Body> unlikeNotice(@RequestParam Long noticeId) {
        return noticeService.unlikePost(noticeId);
    }

}