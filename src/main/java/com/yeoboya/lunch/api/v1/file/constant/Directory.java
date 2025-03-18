package com.yeoboya.lunch.api.v1.file.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Directory {

    NORMAL("normal"),
    PROFILE("profile"),
    BOARD("board"),
    BANNER("banner");

    private final String value;  // ✅ 소문자로 저장

    @Override
    public String toString() {
        return value;  // ✅ Enum 출력 시 소문자 값 반환
    }
}