package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.PinSupport;
import com.yeoboya.lunch.api.v1.board.base.request.PasswordCheckRequest;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractSecretService<T extends AbstractBoard> {

    protected final Response response;
    protected final BoardFetcher<T> boardFetcher;

    public ResponseEntity<Response.Body> verifyPassword(PasswordCheckRequest request) {
        T board = boardFetcher.findById(request.getBoardNo())
                .orElseThrow(() -> new EntityNotFoundException("Board not found - " + request.getBoardNo()));

        if (board instanceof PinSupport) {
            String pin = ((PinSupport) board).getPin();  // ✅ 인터페이스에 정의된 getPin() 사용
            if (!Objects.equals(pin, request.getPassword())) {
                return response.fail(ErrorCode.INVALID_PASSWORD); // ❌ 비밀번호 불일치
            }
            return response.success(Code.SAVE_SUCCESS); // ✅ 비밀번호 일치
        } else {
            throw new UnsupportedOperationException("비밀번호 기능이 없는 게시판입니다.");
        }
    }
}