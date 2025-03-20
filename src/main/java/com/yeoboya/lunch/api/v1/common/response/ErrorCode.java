package com.yeoboya.lunch.api.v1.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    FAIL("FAIL", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_REFRESH_TOKEN("리프레시 토큰이 유효하지 않습니다", BAD_REQUEST),
    INVALID_AUTH_TOKEN("권한 정보가 없는 토큰입니다", UNAUTHORIZED),

    USER_DUPLICATE_EMAIL("사용중인 이메일 입니다.", CONFLICT),
    USER_DUPLICATE_ID("사용중인 아이디 입니다.", CONFLICT),
    USER_NOT_FOUND("아이디 또는 비밀번호를 잘못 입력했습니다", UNAUTHORIZED),

    INVALID_PASSWORD("비밀번호가 일치하지 않습니다. 다시 확인해 주세요.", BAD_REQUEST),
    INVALID_OLD_PASSWORD("현재 비밀번호가 올바르지 않습니다.", BAD_REQUEST),

    DUPLICATE_RESOURCE("데이터가 이미 존재합니다", CONFLICT),

    INVALID_PASSWORD_RESET_LINK("잘못된 비밀번호 재설정 링크를 클릭했습니다", BAD_REQUEST),
    TOO_MANY_REQUESTS("Too Many Requests", HttpStatus.TOO_MANY_REQUESTS),

    FORBIDDEN_FAIL("권한없음", FORBIDDEN),
    NOT_FOUND_FAIL("찾을 수 없음", NOT_FOUND)

    ;
    private final String msg;
    private final HttpStatus httpStatus;


    ErrorCode(String msg, HttpStatus httpStatus) {
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
}
