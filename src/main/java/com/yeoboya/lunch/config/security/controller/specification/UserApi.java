package com.yeoboya.lunch.config.security.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.config.security.reqeust.UserRequest.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "User", description = "사용자 인증 및 보안 관련 API")
public interface UserApi {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/sign-up")
    ResponseEntity<Body> signUp(@Valid @RequestBody SignUp signUp);

    @Operation(summary = "회원가입", description = "소셜 로그인 사용자를 등록합니다.")
    @PostMapping("/social/sign-up")
    ResponseEntity<Body> socialSignUp(@Valid @RequestBody SocialSignUp socialSignUp);

    @Operation(summary = "로그인", description = "사용자 인증을 통해 로그인합니다.")
    @PostMapping("/sign-in")
    ResponseEntity<Body> signIn(@Valid @RequestBody SignIn signIn,
                                HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse);

    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자를 로그아웃합니다.")
    @PostMapping("/sign-out")
    ResponseEntity<Body> signOut(@Valid @RequestBody SignOut signOut,
                                 HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse);

    @Operation(summary = "비밀번호 변경", description = "사용자가 기존 비밀번호를 입력하고 변경합니다.")
    @PatchMapping("/password")
    ResponseEntity<Body> changePassword(@Validated @RequestBody Credentials credentials);

    @Operation(summary = "비밀번호 초기화", description = "사용자가 기존 비밀번호를 모를 경우 비밀번호를 재설정합니다.")
    @PatchMapping("/resetPassword")
    ResponseEntity<Body> resetPassword(@Validated @RequestBody Credentials credentials);

    @Operation(summary = "토큰 재발급", description = "유효한 refreshToken을 이용해 새로운 accessToken을 발급합니다.")
    @PostMapping("/reissue")
    ResponseEntity<Body> reissue(@RequestBody(required = false) Reissue reissue,
                                 @CookieValue(name = "refreshToken", required = false) String refreshTokenFromCookie,
                                 HttpServletResponse httpServletResponse
    );

    @Operation(summary = "비밀번호 변경 이메일 전송", description = "사용자의 이메일로 비밀번호 변경 요청 메일을 전송합니다.")
    @PostMapping("/sendResetPasswordMail")
    ResponseEntity<Body> sendResetPasswordMail(@RequestBody ResetPassword resetPassword);
}
