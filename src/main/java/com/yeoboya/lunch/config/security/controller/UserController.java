package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.config.annotation.TimeLogging;
import com.yeoboya.lunch.config.security.controller.specification.UserApi;
import com.yeoboya.lunch.config.security.reqeust.UserRequest.*;
import com.yeoboya.lunch.config.security.service.UserService;
import com.yeoboya.lunch.config.security.validation.ValidationGroups;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.KnowOldPassword;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.UnKnowOldPassword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;


    /**
     * 일반 회원가입
     */
//    @RateLimited(limit = 1)
    @PostMapping("/sign-up")
    public ResponseEntity<Body> signUp(@Validated(ValidationGroups.NormalSignUpGroup.class) @RequestBody SignUp signUp) {
        return userService.signUp(signUp);
    }

    /**
     * 소셜 회원가입 (update)
     */
    @PostMapping("/social/sign-up")
    public ResponseEntity<Body> socialSignUp(@Valid @RequestBody SocialSignUp socialSignUp, HttpServletResponse response) {
        return userService.socialSignUp(socialSignUp, response);
    }

    /**
     * 일반 로그인
     */
    @PostMapping("/sign-in")
    public ResponseEntity<Body> signIn(@Valid @RequestBody SignIn signIn, HttpServletRequest request, HttpServletResponse response) {
        return userService.signIn(signIn, request, response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/sign-out")
    public ResponseEntity<Body> signOut(@Valid @RequestBody(required = false) SignOut signOut,
                                        HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse) {
        return userService.signOut(signOut, httpServletRequest, httpServletResponse);
    }

    /**
     * 토큰 재발급(only refreshToken)
     */
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody(required = false) Reissue reissue,
                                        @CookieValue(name = "refreshToken", required = false) String refreshTokenFromCookie,
                                        HttpServletResponse response) {

        String refreshToken = null;
        if (reissue != null && StringUtils.hasText(reissue.getRefreshToken())) {
            refreshToken = reissue.getRefreshToken();
        } else if (StringUtils.hasText(refreshTokenFromCookie)) {
            refreshToken = refreshTokenFromCookie;
        }

        return userService.reissue(refreshToken, response);
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/password")
    public ResponseEntity<Body> changePassword(@Validated(KnowOldPassword.class) @RequestBody Credentials credentials) {
        return userService.changePassword(credentials);
    }

    /**
     * 비밀번호 초기화
     */
    @PatchMapping("/resetPassword")
    public ResponseEntity<Body> resetPassword(@Validated(UnKnowOldPassword.class) @RequestBody Credentials credentials) {
        return userService.resetPassword(credentials);
    }

    /**
     * 비밀번호 변경 이메일 전송
     */
    @TimeLogging
    @PostMapping("/sendResetPasswordMail")
    public ResponseEntity<Body> sendResetPasswordMail(@Valid @RequestBody ResetPassword resetPassword) {
        return userService.sendResetPasswordMail(resetPassword);
    }

    /**
     * 아이디 찾기
     */
    @GetMapping("/findLoginId")
    public ResponseEntity<Body> findLoginId(@RequestParam String email) {
        return userService.findLoginId(email);
    }
}
