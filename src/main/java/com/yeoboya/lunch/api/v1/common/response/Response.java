package com.yeoboya.lunch.api.v1.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 공통 API 응답 포맷을 처리하는 유틸 클래스
 */
@Component
public class Response {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "API 응답 객체")
    public static class Body {

        @Schema(description = "HTTP 응답 코드", example = "200")
        private final int code;

        @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
        private final String message;

        @Schema(description = "에러 상세 정보 (선택적)", example = "유효하지 않은 요청입니다.")
        private String detail;

        @Schema(description = "응답 데이터 (선택적)", example = "{ \"id\": 1, \"name\": \"John Doe\" }")
        private Object data;
    }

    // [성공 응답]

    /**
     * 기본 성공 응답 (200, 데이터 없음)
     */
    public ResponseEntity<Body> success() {
        return success(HttpStatus.OK, null, Collections.emptyList());
    }

    /**
     * 메시지만 포함하는 성공 응답
     */
    public ResponseEntity<Body> success(String msg) {
        return success(HttpStatus.OK, msg, Collections.emptyList());
    }

    /**
     * 데이터만 포함하는 성공 응답
     */
    public ResponseEntity<Body> success(Object data) {
        return success(HttpStatus.OK, null, data);
    }

    /**
     * 메시지와 데이터를 포함하는 성공 응답
     */
    public ResponseEntity<Body> success(String msg, Object data) {
        return success(HttpStatus.OK, msg, data);
    }

    /**
     * 사용자 정의 코드 및 메시지를 포함하는 성공 응답
     */
    public ResponseEntity<Body> success(Code code, Object data) {
        return success(code.getHttpStatus(), code.getMsg(), data);
    }

    /**
     * 전체 커스텀 성공 응답 (상태 코드 + 메시지 + 데이터)
     */
    public ResponseEntity<Body> success(HttpStatus status, String msg, Object data) {
        Body body = Body.builder()
                .code(status.value())
                .message(msg)
                .data(data)
                .build();
        return ResponseEntity.status(status.value()).body(body);
    }

    // [실패 응답]

    /**
     * 기본 실패 응답 (상태 코드, 메시지만 포함)
     */
    public ResponseEntity<Body> fail(ErrorCode errorCode) {
        Body body = Body.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMsg())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    /**
     * 상세 메시지를 포함한 실패 응답
     */
    public ResponseEntity<Body> fail(ErrorCode errorCode, String detail) {
        Body body = Body.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMsg())
                .detail(detail)
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    /**
     * 데이터(에러 목록 등)를 포함한 실패 응답
     */
    public ResponseEntity<Body> fail(ErrorCode errorCode, Object data) {
        Body body = Body.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMsg())
                .data(data)
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }
}