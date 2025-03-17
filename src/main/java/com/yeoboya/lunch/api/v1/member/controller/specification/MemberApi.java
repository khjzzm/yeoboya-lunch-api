package com.yeoboya.lunch.api.v1.member.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import com.yeoboya.lunch.api.v1.member.response.AccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Tag(name = "Member", description = "회원 관리 관련 API")
public interface MemberApi {

    @Operation(summary = "멤버 리스트 조회", description = "모든 멤버 리스트를 조회합니다.")
    @GetMapping
    ResponseEntity<Body> member(SearchMember searchMember, Pageable pageable);

    @Operation(summary = "멤버 요약 정보 조회", description = "특정 멤버의 요약 정보를 조회합니다.")
    @GetMapping("{memberLoginId}/summary")
    ResponseEntity<Body> getMemberSummary(@PathVariable String memberLoginId);

    @Operation(summary = "회원 검색 (프로필 정보)", description = "특정 멤버의 프로필 정보를 조회합니다.")
    @GetMapping("{memberLoginId}/profile")
    ResponseEntity<Body> getMemberProfile(@PathVariable String memberLoginId);

    @Operation(summary = "멤버 계좌 정보 조회", description = "특정 멤버의 계좌 정보를 조회합니다.")
    @GetMapping("/account/{memberLoginId}")
    ResponseEntity<Body> findAccountMember(@PathVariable String memberLoginId);

    @Operation(summary = "멤버 상세 정보 수정", description = "특정 멤버의 상세 정보를 수정합니다.")
    @PatchMapping("/setting/info/{memberLoginId}")
    ResponseEntity<Body> editMemberInfo(@PathVariable String memberLoginId, @RequestBody MemberInfoEdit memberInfoEdit);

    @Operation(summary = "멤버 계좌 등록", description = "새로운 계좌 정보를 등록합니다.")
    @PostMapping("/account")
    ResponseEntity<Body> account(@RequestBody @Valid AccountCreate accountCreate);

    @Operation(summary = "멤버 계좌 수정", description = "특정 멤버의 계좌 정보를 수정합니다.")
    @PatchMapping("/account/{memberLoginId}")
    ResponseEntity<Body> accountUpdate(@PathVariable String memberLoginId, @RequestBody AccountEdit accountEdit);

    @Operation(summary = "멤버 프로필 사진 등록", description = "특정 멤버의 프로필 사진을 업로드합니다.")
    @PostMapping("/profile-image")
    ResponseEntity<Body> updateProfileImage(@RequestParam("file") MultipartFile file, @RequestPart @Valid MemberProfile memberProfile);

    @Operation(summary = "대표 이미지 설정", description = "특정 프로필 이미지를 대표 이미지로 설정합니다.")
    @PostMapping("/profile-image/default/{imageNo}")
    ResponseEntity<Body> updateDefaultProfileImage(@PathVariable Long imageNo);
}