package com.yeoboya.lunch.config.cache.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    /**
     * 랜덤 데이터 캐싱 (예제용)
     * - cacheName: "random"
     * - expiredAfterWrite: 60초 후 만료 (1분)
     * - maximumSize: 최대 10,000개까지 캐싱 가능
     */
    RANDOM("random", 60, 10000),

    /**
     * 리소스 목록 캐싱 (권한-URL 매핑 정보)
     * - cacheName: "resourceList"
     * - expiredAfterWrite: 60초 후 만료 (1분마다 최신 데이터 반영)
     * - maximumSize: 최대 10,000개까지 캐싱 가능
     */
    RESOURCE("resourceList", 60, 10000),

    /**
     * 접근 가능한 IP 목록 캐싱 (IP 제한 정책)
     * - cacheName: "accessIpList"
     * - expiredAfterWrite: 60초 후 만료 (1분마다 최신 IP 반영)
     * - maximumSize: 최대 10,000개까지 캐싱 가능
     */
    ACCESS_IP("accessIpList", 60, 10000);

    /**
     * cacheName: 캐시 이름 (Spring Cache에서 사용하는 키 값)
     * expiredAfterWrite: 캐시 만료 시간 (초 단위)
     * maximumSize: 캐시에 저장할 최대 엔트리 수 (초과 시 자동 제거)
     */
    private final String cacheName;
    private final int expiredAfterWrite; // 캐시 만료 시간 (초 단위)
    private final int maximumSize; // 캐시에 저장할 최대 개수
}