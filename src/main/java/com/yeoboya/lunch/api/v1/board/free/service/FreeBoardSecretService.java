package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.service.AbstractSecretService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.common.response.Response;
import org.springframework.stereotype.Service;

@Service
public class FreeBoardSecretService extends AbstractSecretService<FreeBoard> {

    public FreeBoardSecretService(Response response, BoardFetcher<FreeBoard> boardFetcher) {
        super(response, boardFetcher);
    }

}