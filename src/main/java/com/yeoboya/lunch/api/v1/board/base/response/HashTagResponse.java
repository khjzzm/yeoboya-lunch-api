package com.yeoboya.lunch.api.v1.board.base.response;


import com.querydsl.core.annotations.QueryProjection;
import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class HashTagResponse {

    private final String tag;
    private Long count;

    public static HashTagResponse from(HashTag hashTag) {
        return new HashTagResponse(hashTag.getTag(), 0L);
    }

    @QueryProjection
    public HashTagResponse(String tag, Long count) {
        this.tag = tag;
        this.count = count != null ? count.intValue() : 0L;
    }
}
