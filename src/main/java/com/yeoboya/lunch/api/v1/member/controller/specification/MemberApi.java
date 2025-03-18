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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Member", description = "회원 관리 관련 API")
public interface MemberApi {

    @Operation(summary = "멤버 리스트 조회", description = "모든 멤버 리스트를 조회합니다.")
    @GetMapping
    ResponseEntity<Body> member(SearchMember searchMember, Pageable pageable);

    @Operation(summary = "회원 검색 (프로필 정보)", description = "특정 멤버의 프로필 정보를 조회합니다.")
    @GetMapping("{memberLoginId}")
    ResponseEntity<Body> getMemberProfile(@PathVariable String memberLoginId);

    @Operation(summary = "멤버 계좌 정보 조회", description = "특정 멤버의 계좌 정보를 조회합니다.")
    @GetMapping("/account/{memberLoginId}")
    ResponseEntity<Body> findAccountMember(@PathVariable String memberLoginId);

}