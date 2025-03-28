package com.yeoboya.lunch.api.v1.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 일반 에러
    FAIL("FAIL", HttpStatus.INTERNAL_SERVER_ERROR),

    // 인증/인가 관련
    INVALID_REFRESH_TOKEN("리프레시 토큰이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_AUTH_TOKEN("권한 정보가 없는 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 사용자 관련
    USER_DUPLICATE_EMAIL("사용중인 이메일 입니다.", HttpStatus.CONFLICT),
    USER_DUPLICATE_ID("사용중인 아이디 입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("아이디 또는 비밀번호를 잘못 입력했습니다", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다. 다시 확인해 주세요.", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD("현재 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 검증 관련
    VALIDATION_ERROR("요청값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // DB 관련
    DUPLICATE_RESOURCE("데이터가 이미 존재합니다", HttpStatus.CONFLICT),
    DATA_INTEGRITY_VIOLATION("데이터 무결성 위반입니다.", HttpStatus.CONFLICT),

    // 요청 제한
    TOO_MANY_REQUESTS("요청이 너무 많습니다.", HttpStatus.TOO_MANY_REQUESTS),

    // 권한/리소스
    FORBIDDEN("접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // 요청 형식 오류
    METHOD_NOT_ALLOWED_ERROR("허용되지 않은 HTTP 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT_ERROR("요청이 충돌되었습니다", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR_OCCURRED("서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 본문 오류
    INVALID_REQUEST_BODY("요청 내용을 읽을 수 없습니다", HttpStatus.BAD_REQUEST)

    ;
    private final String msg;
    private final HttpStatus httpStatus;

    ErrorCode(String msg, HttpStatus httpStatus) {
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
}