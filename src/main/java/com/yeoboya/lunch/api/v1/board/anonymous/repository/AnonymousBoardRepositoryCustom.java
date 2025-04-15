package com.yeoboya.lunch.api.v1.board.anonymous.repository;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;

public interface AnonymousBoardRepositoryCustom {
    Slice<AnonymousBoard> findAllBySlice(Pageable pageable);
}