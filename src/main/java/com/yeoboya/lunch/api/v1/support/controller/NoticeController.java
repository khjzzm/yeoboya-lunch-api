package com.yeoboya.lunch.api.v1.support.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.NoticeFileResponse;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import com.yeoboya.lunch.api.v1.support.response.NoticeResponseDTO;
import com.yeoboya.lunch.api.v1.support.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final FileServiceS3 fileServiceS3;
    private final Response response;

    @PostMapping("/notices")
    public ResponseEntity<Response.Body> createNotice(@Valid @RequestBody NoticeRequest noticeRequest) {
        Notice notice = noticeService.createNotice(noticeRequest);
        return response.success(Code.SAVE_SUCCESS, notice);
    }

    @PostMapping("/notice/image")
    public ResponseEntity<Response.Body> uploadImage(@RequestParam MultipartFile file) {
        Function<FileResponse, NoticeFileResponse> responseMapper = NoticeFileResponse::apply;
        FileResponse fileResponse = fileServiceS3.upload(file, Directory.NOTICE, responseMapper);
        return response.success(Code.SAVE_SUCCESS, fileResponse);
    }

    @PostMapping("/notice/read")
    public ResponseEntity<Response.Body> markNoticeAsRead(@RequestParam Long noticeId, @RequestParam String loginId) {
        noticeService.markNoticeAsRead(noticeId, loginId);
        return response.success(Code.SEARCH_SUCCESS);
    }

    @GetMapping("/notices")
    public ResponseEntity<Response.Body> getAllBoardsWithReadStatus(@RequestParam(required = false) String loginId) {
        List<NoticeResponseDTO> notices = noticeService.getAllNoticesWithReadStatus(loginId);
        return response.success(Code.SEARCH_SUCCESS, notices);
    }

    @GetMapping("/notice")
    public ResponseEntity<Response.Body> getNoticeDetail(@RequestParam Long noticeId) {
        NoticeResponseDTO detail = noticeService.getNoticeDetail(noticeId);
        return response.success(Code.SEARCH_SUCCESS, detail);
    }


    @PutMapping("/notice")
    public ResponseEntity<Response.Body> updateNotice(@RequestParam Long noticeId,
                                                      @Valid @RequestBody NoticeRequest noticeRequest) {
        Notice updated = noticeService.updateNotice(noticeId, noticeRequest);
        return response.success(Code.UPDATE_SUCCESS, updated);
    }

    @DeleteMapping("/notice/delete")
    public ResponseEntity<Response.Body> deleteNotice(@RequestParam Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return response.success(Code.DELETE_SUCCESS);
    }

}