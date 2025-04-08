package com.yeoboya.lunch.api.v1.board.base.repository.tag;

import com.yeoboya.lunch.api.v1.board.base.response.HashTagResponse;

import java.util.List;

public interface HashTagRepositoryCustom {
    List<HashTagResponse> findTopHashtags(String keyword, int limit);
}
