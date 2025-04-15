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

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class AnonymousBoardController {

    private final AnonymousBoardService anonymousBoardService;
    private final Response response;

    // 게시글 작성
    @PostMapping("/anonymous")
    public ResponseEntity<Response.Body> write(@RequestBody AnonymousBoardCreate anonymousBoardCreate, HttpServletRequest request) {
        AnonymousBoardResponse anonymousBoardResponse = anonymousBoardService.create(anonymousBoardCreate, request);
        return response.success(Code.SAVE_SUCCESS, anonymousBoardResponse);
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
    public ResponseEntity<Response.Body> report(@RequestBody AnonymousBoardReport anonymousBoardReport) {
        anonymousBoardService.report(anonymousBoardReport);
        return response.success(Code.SAVE_SUCCESS);
    }

    // 게시글 조회 (slice)
    @GetMapping("/anonymous")
    public ResponseEntity<Response.Body> anonymous(Pageable pageable) {
        Map<String, Object> anonymousBoards = anonymousBoardService.getAnonymousBoards(pageable);
        return response.success(Code.SEARCH_SUCCESS, anonymousBoards);
    }

}