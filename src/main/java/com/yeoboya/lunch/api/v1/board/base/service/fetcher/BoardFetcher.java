package com.yeoboya.lunch.api.v1.board.base.service.fetcher;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;

import java.util.Optional;

public interface BoardFetcher<T extends AbstractBoard> {
    Optional<T> findById(Long id);
}