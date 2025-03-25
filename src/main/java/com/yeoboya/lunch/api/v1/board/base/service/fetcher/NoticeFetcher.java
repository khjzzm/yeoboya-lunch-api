package com.yeoboya.lunch.api.v1.board.base.service.fetcher;

import com.yeoboya.lunch.api.v1.support.domain.Notice;
import com.yeoboya.lunch.api.v1.support.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NoticeFetcher implements BoardFetcher<Notice> {

    private final NoticeRepository noticeRepository;

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeRepository.findById(id);
    }

}