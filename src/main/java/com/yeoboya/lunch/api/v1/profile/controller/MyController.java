package com.yeoboya.lunch.api.v1.profile.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountCreate;
import com.yeoboya.lunch.api.v1.member.reqeust.AccountEdit;
import com.yeoboya.lunch.api.v1.member.reqeust.MemberInfoEdit;
import com.yeoboya.lunch.api.v1.member.response.AccountResponse;
import com.yeoboya.lunch.api.v1.profile.controller.specification.MyApi;
import com.yeoboya.lunch.api.v1.profile.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MyController implements MyApi {

    private final Response response;
    private final MyService myService;

    /**
     * 내 정보 가져오기
     */
    @GetMapping("/my-info")
    public ResponseEntity<Body> getMyInformation(HttpServletRequest request) {
        return response.success(Code.SEARCH_SUCCESS, myService.getMyInformation(request));
    }

    /**
     * 상세 정보 수정
     */
    @PatchMapping("/my-info")
    public ResponseEntity<Body> editMyInfo(@RequestBody MemberInfoEdit memberInfoEdit, HttpServletRequest request) {
        myService.editMyInfo(memberInfoEdit, request);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 계좌 등록
     */
    @PostMapping("/account")
    public ResponseEntity<Body> saveMyAccount(@RequestBody @Valid AccountCreate accountCreate, HttpServletRequest request) {
        AccountResponse accountResponse = myService.saveMyAccount(accountCreate, request);
        return response.success(Code.SAVE_SUCCESS, accountResponse);
    }

    /**
     * 계좌 수정
     */
    @PatchMapping("/account")
    public ResponseEntity<Body> editAccountInfo(@RequestBody AccountEdit accountEdit, HttpServletRequest request) {
        myService.editAccountInfo(accountEdit, request);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 프로필 사진 등록
     */
    @PostMapping("/profile-image")
    public ResponseEntity<Body> updateProfileImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return myService.updateProfileImage(file, request);
    }

    /**
     * 대표 이미지 설정
     */
    @PostMapping("/profile-image/default/{imageNo}")
    public ResponseEntity<Body> updateDefaultProfileImage(@PathVariable Long imageNo) {
        return myService.setDefaultProfileImage(imageNo);
    }
}
