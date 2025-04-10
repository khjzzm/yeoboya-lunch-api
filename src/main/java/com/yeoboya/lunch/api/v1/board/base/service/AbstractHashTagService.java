package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractHashTagService<T extends AbstractBoard> {

    protected final RedisUtil redisUtil;
    protected final CacheManager cacheManager;
    protected final HashTagRepository hashTagRepository;
    protected final Response response;

    @Transactional
    public List<BoardHashTag> createBoardHashTags(List<String> tags) {
        return Optional.ofNullable(tags)
                .orElse(Collections.emptyList())
                .stream()
                .map(tag -> hashTagRepository.findHashTagByTag(tag)
                        .orElseGet(() -> hashTagRepository.save(HashTag.builder().tag(tag).build())))
                .map(BoardHashTag::createBoardHashTag)
                .collect(Collectors.toList());
    }

    @Cacheable(
            value = "hashtagSearch",
            key = "#keyword",
            unless = "#result.body.data == null || #result.body.data.isEmpty()"
    )
    public ResponseEntity<Response.Body> search(String keyword) {
        List<HashTagResponse> topHashtags = hashTagRepository.findTopHashtags(keyword, 10);
        return response.success(Code.SEARCH_SUCCESS, topHashtags);
    }

    public void updateHashtagCacheAndScore(List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        for (String tag : tags) {
            redisUtil.incrementHashtagScore(tag); // 인기 태그 점수 +1
            // Spring Cache (@Cacheable 캐시) 삭제
            Objects.requireNonNull(cacheManager.getCache("hashtagSearch"))
                    .evict(tag); // key = keyword 와 일치해야 함

            // Redis 직접 캐시 삭제 (정규식 삭제용)
            redisUtil.delAsterOps("hashtagSearch*" + tag);
        }
    }

    public ResponseEntity<Response.Body> getTopHashtagsWithScore(int limit) {
        String zsetKey = "popular:hashtag";

        Set<ZSetOperations.TypedTuple<String>> results =
                redisUtil.getZSetWithScore(zsetKey, 0, limit - 1);

        if (results == null || results.isEmpty()) {
            return response.success(Code.SEARCH_SUCCESS, List.of());
        }

        List<HashTagResponse> collect = results.stream()
                .filter(entry -> entry.getValue() != null && entry.getScore() != null)
                .map(entry -> new HashTagResponse(entry.getValue(), entry.getScore().longValue()))
                .collect(Collectors.toList());
        return response.success(Code.SEARCH_SUCCESS, collect);
    }

}