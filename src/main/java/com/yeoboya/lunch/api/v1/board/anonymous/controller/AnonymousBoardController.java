// Controller
package com.yeoboya.lunch.api.v1.board.anonymous.controller;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardCreate;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardDelete;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardReport;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardUpdate;
import com.yeoboya.lunch.api.v1.board.anonymous.response.AnonymousBoardResponse;
import com.yeoboya.lunch.api.v1.board.anonymous.service.AnonymousBoardService;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class AnonymousBoardController {

    private final AnonymousBoardService anonymousBoardService;
    private final Response response;

    // 게시글 작성
    @PostMapping("/anonymous")
    public ResponseEntity<Response.Body> write(@RequestBody AnonymousBoardCreate request,
                                               @RequestHeader(value = "X-Anonymous-UUID", required = false) String uuid,
                                               HttpServletRequest servletRequest) {
        AnonymousBoardResponse res = anonymousBoardService.create(request, uuid, servletRequest);
        return response.success(Code.SAVE_SUCCESS, res);
    }


    // 게시글 수정
    @PutMapping("/anonymous")
    public ResponseEntity<Response.Body> edit(@RequestBody AnonymousBoardUpdate anonymousBoardUpdate) {
        AnonymousBoardResponse update = anonymousBoardService.update(anonymousBoardUpdate);
        return response.success(Code.UPDATE_SUCCESS, update);
    }

    // 게시글 삭제
    @DeleteMapping("/anonymous")
    public ResponseEntity<Response.Body> delete(@RequestBody AnonymousBoardDelete anonymousBoardDelete) {
        anonymousBoardService.delete(anonymousBoardDelete);
        return response.success(Code.DELETE_SUCCESS);
    }

    // 게시글 신고
    @PostMapping("/anonymous/report")
    public ResponseEntity<Response.Body> report(@RequestBody AnonymousBoardReport anonymousBoardReport,
                                                @RequestHeader("X-Anonymous-UUID") String clientUUID) {
        anonymousBoardService.report(anonymousBoardReport, clientUUID);
        return response.success(Code.SAVE_SUCCESS);
    }

    // 게시글 조회 (slice)
    @GetMapping("/anonymous")
    public ResponseEntity<Response.Body> anonymous(Pageable pageable,
                                                   @RequestHeader(value = "X-Anonymous-UUID", required = false) String uuid) {
        Map<String, Object> anonymousBoards = anonymousBoardService.getAnonymousBoards(pageable, uuid);
        return response.success(Code.SEARCH_SUCCESS, anonymousBoards);
    }

    // 새글 등록 탐지
    @GetMapping("/anonymous/has-new-detect")
    public ResponseEntity<Response.Body> hasNewAnonymousPost(
            @RequestHeader(value = "X-Anonymous-UUID", required = false) String uuid) {
        boolean hasNew = anonymousBoardService.hasNewPostForClient(uuid);
        return response.success(Code.SEARCH_SUCCESS, hasNew);
    }

    // 최신글 UUID 동기화
    @PostMapping("/anonymous/latest-sync")
    public ResponseEntity<Response.Body> syncLatestForClient(@RequestHeader("X-Anonymous-UUID") String uuid) {
        Long syncedPostId = anonymousBoardService.syncLatestForClient(uuid);
        return response.success(Code.UPDATE_SUCCESS, syncedPostId);
    }

    // 좋아요
    @PostMapping("/anonymous/like")
    public ResponseEntity<Response.Body> likeAnonymousBoard(@RequestParam Long boardNo,
                                                            @RequestHeader("X-Anonymous-UUID") String clientUUID) {
        return anonymousBoardService.likePost(boardNo, clientUUID);
    }


}