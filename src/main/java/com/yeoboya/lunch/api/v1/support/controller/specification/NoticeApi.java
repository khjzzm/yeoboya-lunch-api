package com.yeoboya.lunch.api.v1.support.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.support.request.NoticeCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Support", description = "공지사항 관리 API")
@RequestMapping("/support")
public interface NoticeApi {

    @Operation(summary = "공지사항 생성")
    @PostMapping("/notices")
    ResponseEntity<Response.Body> createNotice(@Valid @RequestBody NoticeCreate noticeCreate);

    @Operation(summary = "공지사항 읽음 처리")
    @PostMapping("/notices/{noticeId}/mark-as-read")
    ResponseEntity<Response.Body> markNoticeAsRead(@PathVariable Long noticeId, @RequestParam String loginId);

    @Operation(summary = "공지사항 조회")
    @GetMapping("/notices")
    ResponseEntity<Response.Body> getAllBoardsWithReadStatus(@RequestParam String loginId);
}