package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.repository.tag.BoardHashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractHashTagService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.config.redis.RedisUtil;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class FreeBoardHashTagService extends AbstractHashTagService<FreeBoard> {

    public FreeBoardHashTagService(RedisUtil redisUtil, CacheManager cacheManager, HashTagRepository hashTagRepository, BoardHashTagRepository boardHashTagRepository, BoardFetcher<FreeBoard> boardFetcher) {
        super(redisUtil, cacheManager, hashTagRepository, boardHashTagRepository, boardFetcher);
    }
}
