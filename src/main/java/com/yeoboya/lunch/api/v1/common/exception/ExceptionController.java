package com.yeoboya.lunch.api.v1.common.exception;

import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.ValidationError;
import com.yeoboya.lunch.config.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ExceptionController {

    private final Response response;

    // 1. DTO 유효성 검증 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response.Body> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        List<ValidationError> errors = ValidationUtils.refineErrors(e);
        return response.fail(ErrorCode.FAIL, errors);
    }

    // 2. @RequestParam, @PathVariable 유효성 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response.Body> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        List<ValidationError> errors = ValidationUtils.refineErrors(e);
        return response.fail(ErrorCode.FAIL, errors);
    }

    // 3. Form 데이터 바인딩 실패 (@ModelAttribute)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response.Body> handleBindException(BindException e) {
        log.error("BindException", e);
        List<ValidationError> errors = ValidationUtils.refineErrors(e);
        return response.fail(ErrorCode.FAIL, errors);
    }

    // 4. 필수 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response.Body> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        return response.fail(ErrorCode.FAIL, e.getMessage());
    }

    // 5. 경로 변수 누락
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Response.Body> handleMissingPathVariable(MissingPathVariableException e) {
        log.error("MissingPathVariableException", e);
        return response.fail(ErrorCode.FAIL, e.getMessage());
    }

    // 6. JSON 파싱 오류 (날짜 포맷, enum 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response.Body> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);
        return response.fail(ErrorCode.FAIL, "요청 JSON이 올바르지 않습니다.");
    }

    // 7. Content-Type 미지원
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response.Body> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException", e);
        return response.fail(ErrorCode.FAIL, "지원하지 않는 미디어 타입입니다.");
    }

    // 8. 허용되지 않은 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response.Body> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        return response.fail(ErrorCode.METHOD_NOT_ALLOWED_ERROR);
    }

    // 9. 파일 업로드 용량 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response.Body> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException", e);
        return response.fail(ErrorCode.FAIL, "파일 크기가 너무 큽니다.");
    }

    // 10. DB 제약조건 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response.Body> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException", e);
        return response.fail(ErrorCode.CONFLICT_ERROR, e.getMostSpecificCause().getMessage());
    }

    // 11. 존재하지 않는 리소스 접근
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Response.Body> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        log.error("EmptyResultDataAccessException", e);
        return response.fail(ErrorCode.NOT_FOUND);
    }

    // 12. 데이터베이스 오류
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response.Body> handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException", e);
        return response.fail(ErrorCode.INTERNAL_SERVER_ERROR_OCCURRED, e.getMostSpecificCause().getMessage());
    }

    // 13. 권한 거부
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response.Body> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException", e);
        return response.fail(ErrorCode.FORBIDDEN, "접근 권한이 없습니다.");
    }

    // 14. 비즈니스 예외 처리
    @ExceptionHandler(LunchException.class)
    public ResponseEntity<Response.Body> handleLunchException(LunchException e) {
        log.error("LunchException", e);
        return ResponseEntity.status(e.getStatusCode()).body(
                Response.Body.builder()
                        .code(e.getStatusCode())
                        .message(e.getMessage())
                        .build()
        );
    }
}
