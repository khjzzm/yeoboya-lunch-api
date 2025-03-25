package com.yeoboya.lunch.api.v1.board.base.service.fetcher;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FreeBoardFetcher implements BoardFetcher<FreeBoard> {

    private final FreeBoardRepository freeBoardRepository;

    @Override
    public Optional<FreeBoard> findById(Long id) {
        return freeBoardRepository.findById(id);
    }

}