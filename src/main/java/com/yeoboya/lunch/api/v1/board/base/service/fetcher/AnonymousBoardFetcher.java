package com.yeoboya.lunch.api.v1.board.base.service.fetcher;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.board.anonymous.repository.AnonymousBoardRepository;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import com.yeoboya.lunch.api.v1.support.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnonymousBoardFetcher implements BoardFetcher<AnonymousBoard> {

    private final AnonymousBoardRepository anonymousBoardRepository;

    @Override
    public Optional<AnonymousBoard> findById(Long id) {
        return anonymousBoardRepository.findById(id);
    }

}