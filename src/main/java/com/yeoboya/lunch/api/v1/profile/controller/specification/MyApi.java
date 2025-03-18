package com.yeoboya.lunch.api.v1.profile.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "My", description = "내 정보 관리 관련 API")
public interface MyApi {

    @Operation(summary = "내 정보 가져오기", description = "내 정보를 가져옵니다")
    @GetMapping("/my-info")
    ResponseEntity<Body> getMyInformation(HttpServletRequest request);

    @Operation(summary = "상세 정보 수정", description = "상세 정보를 수정합니다.")
    @PatchMapping("/my-info")
    ResponseEntity<Body> editMyInfo(@RequestBody MemberInfoEdit memberInfoEdit, HttpServletRequest request);

    @Operation(summary = "계좌 등록", description = "새로운 계좌 정보를 등록합니다.")
    @PostMapping("/account")
    ResponseEntity<Body> saveMyAccount(@RequestBody @Valid AccountCreate accountCreate, HttpServletRequest request);

    @Operation(summary = "계좌 수정", description = "계좌 정보를 수정합니다.")
    @PatchMapping("/account")
    ResponseEntity<Body> editAccountInfo(@RequestBody AccountEdit accountEdit, HttpServletRequest request);

    @Operation(summary = "프로필 사진 등록", description = "프로필 사진을 업로드합니다.")
    @PostMapping("/profile-image")
    ResponseEntity<Body> updateProfileImage(@RequestParam("file") MultipartFile file, HttpServletRequest request);

    @Operation(summary = "대표 이미지 설정", description = "특정 프로필 이미지를 대표 이미지로 설정합니다.")
    @PostMapping("/profile-image/default/{imageNo}")
    ResponseEntity<Body> updateDefaultProfileImage(@PathVariable Long imageNo);

}