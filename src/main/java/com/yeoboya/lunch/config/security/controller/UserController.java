package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.config.annotation.TimeLogging;
import com.yeoboya.lunch.config.security.controller.specification.UserApi;
import com.yeoboya.lunch.config.security.reqeust.UserRequest.*;
import com.yeoboya.lunch.config.security.service.UserService;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.KnowOldPassword;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.UnKnowOldPassword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;


    /**
     * 회원가입
     */
//    @RateLimited(limit = 1)
    @PostMapping("/sign-up")
    public ResponseEntity<Body> signUp(@Valid @RequestBody SignUp signUp) {
        return userService.signUp(signUp);
    }

    /**
     * 로그인
     *
     */
    @PostMapping("/sign-in")
    public ResponseEntity<Body> signIn(@Valid @RequestBody SignIn signIn, HttpServletRequest httpServletRequest) {
        return userService.signIn(signIn, httpServletRequest);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/sign-out")
    public ResponseEntity<Body> signOut(@Valid @RequestBody SignOut signOut) {
        return userService.signOut(signOut);
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/setting/security")
    public ResponseEntity<Body> changePassword(@Validated(KnowOldPassword.class) @RequestBody Credentials credentials){
        return userService.changePassword(credentials);
    }

    /**
     * 비밀번호 초기화
     */
    @PatchMapping("/resetPassword")
    public ResponseEntity<Body> resetPassword(@Validated(UnKnowOldPassword.class) @RequestBody Credentials credentials){
        return userService.resetPassword(credentials);
    }

    /**
     * 토큰 재발급(only refreshToken)
     */
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@Valid @RequestBody Reissue reissue){
        return userService.reissue(reissue);
    }

    /**
     * 비밀번호 변경 이메일 전송
     */
    @TimeLogging
    @PostMapping("/sendResetPasswordMail")
    public ResponseEntity<Body> sendResetPasswordMail(@RequestBody ResetPassword resetPassword) {
        return userService.sendResetPasswordMail(resetPassword);
    }
}
