package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.repository.tag.BoardHashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractHashTagService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import org.springframework.stereotype.Service;

@Service
public class FreeBoardHashTagService extends AbstractHashTagService<FreeBoard> {

    public FreeBoardHashTagService(HashTagRepository hashTagRepository, BoardHashTagRepository boardHashTagRepository, BoardFetcher<FreeBoard> boardFetcher) {
        super(hashTagRepository, boardHashTagRepository, boardFetcher);
    }
}
