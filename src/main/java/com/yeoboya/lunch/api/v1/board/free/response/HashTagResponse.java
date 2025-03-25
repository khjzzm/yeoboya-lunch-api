package com.yeoboya.lunch.api.v1.board.free.response;


import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class HashTagResponse {

    private final String tag;

    public static HashTagResponse from(HashTag hashTag){
        return new HashTagResponse(hashTag.getTag());
    }

}
