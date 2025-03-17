package com.yeoboya.lunch.api.v1.member.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.member.controller.specification.MemberApi;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import com.yeoboya.lunch.api.v1.member.response.AccountResponse;
import com.yeoboya.lunch.api.v1.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberApi {

    private final Response response;
    private final MemberService memberService;

    /**
     * 멤버 리스트
     */
    @GetMapping
    public ResponseEntity<Body> member(@ModelAttribute SearchMember searchMember, Pageable pageable) {
        return response.success(Code.SEARCH_SUCCESS, memberService.memberList(searchMember, pageable));
    }

    @GetMapping("{memberLoginId}")
    public ResponseEntity<Body> getMemberSummary(@PathVariable String memberLoginId) {
        return response.success(Code.SEARCH_SUCCESS, memberService.memberSummary(memberLoginId));
    }

    /**
     * 회원검색 (계좌/프로필사진)
     */
    @GetMapping("{memberLoginId}/profile")
    public ResponseEntity<Body> getMemberProfile(@PathVariable String memberLoginId) {
        return response.success(Code.SEARCH_SUCCESS, memberService.memberProfile(memberLoginId));
    }

    /**
     * 멤버 정보 검색(기본/계좌)
     */
    @GetMapping("/account/{memberLoginId}")
    public ResponseEntity<Body> findAccountMember(@PathVariable String memberLoginId) {
        return response.success(Code.SEARCH_SUCCESS, memberService.memberAccount(memberLoginId));
    }

    /**
     * 멤버 상세 정보 수정
     */
    @PatchMapping("/setting/info/{memberLoginId}")
    public ResponseEntity<Body> editMemberInfo(@PathVariable String memberLoginId, @RequestBody MemberInfoEdit memberInfoEdit) {
        memberService.editMemberInfo(memberLoginId, memberInfoEdit);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 멤버 계좌 등록
     */
    @PostMapping("/account")
    public ResponseEntity<Body> account(@RequestBody @Valid AccountCreate accountCreate) {
        AccountResponse accountResponse = memberService.addAccount(accountCreate);
        return response.success(Code.SAVE_SUCCESS, accountResponse);
    }

    /**
     * 멤버 계좌 수정
     */
    @PatchMapping("/account/{memberLoginId}")
    public ResponseEntity<Body> accountUpdate(@PathVariable String memberLoginId, @RequestBody AccountEdit accountEdit) {
        memberService.editAccount(memberLoginId, accountEdit);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 멤버 프로필 사진 등록
     */
    @PostMapping("/profile-image")
    public ResponseEntity<Body> updateProfileImage(@RequestParam("file") MultipartFile file, @RequestPart @Valid MemberProfile memberProfile) {
        return memberService.updateProfileImage(file, memberProfile);
    }

    /**
     * 대표 이미지 설정
     */
    @PostMapping("/profile-image/default/{imageNo}")
    public ResponseEntity<Body> updateDefaultProfileImage(@PathVariable Long imageNo) {
        return memberService.setDefaultProfileImage(imageNo);
    }

}
