package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.config.security.domain.Resources;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicResourceService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ResourcesRepository resourcesRepository;

    /**
     * Spring Security 리소스(자원) 목록을 자동으로 스캔하여 DB에 반영한다.
     * 새로운 리소스가 발견되면 자동으로 추가된다.
     */
    @Transactional
    public void syncResources() {
        log.warn("동적 리소스 동기화 시작...");

        // 현재 DB에 저장된 리소스 목록 조회
        Set<String> existingResources = resourcesRepository.findAll()
                .stream()
                .map(Resources::getResourceName)
                .collect(Collectors.toSet());

        // 기존 데이터: "resourceName + httpMethod" 형태로 저장하여 중복 체크
        Set<String> existingResourceKeys = resourcesRepository.findAll()
                .stream()
                .map(res -> res.getResourceName() + "_" + (res.getHttpMethod() == null ? "ALL" : res.getHttpMethod()))
                .collect(Collectors.toSet());

        // 확인용 출력
        requestMappingHandlerMapping.getHandlerMethods().forEach((info, method) -> {
            log.debug("🔍 발견된 리소스: {} | HTTP 메서드: {}", info.getDirectPaths(), info.getMethodsCondition().getMethods());
        });

        // 새롭게 추가해야 할 리소스 찾기
        Set<String> existingResourcesSet = new HashSet<>();

        List<Resources> newResources = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> entry.getKey().getDirectPaths().stream()
                        .map(url -> new AbstractMap.SimpleEntry<>(url, entry.getKey().getMethodsCondition().getMethods()))
                )
                .map(entry -> {
                    String httpMethods = entry.getValue().isEmpty() ? "ALL" : entry.getValue().stream().map(Enum::name).collect(Collectors.joining(", "));

                    String uniqueKey = entry.getKey() + "|" + httpMethods;
                    if (existingResourcesSet.contains(uniqueKey)) {
                        return null; // 중복 방지
                    }
                    existingResourcesSet.add(uniqueKey);

                    return Resources.builder()
                            .resourceName(entry.getKey())
                            .resourceType("url")
                            .orderNum(999) // 기본 orderNum
                            .httpMethod(httpMethods)
                            .build();
                })
                .filter(Objects::nonNull) // null 제거
                .collect(Collectors.toList());

        if (!newResources.isEmpty()) {
            resourcesRepository.saveAll(newResources);
            log.warn("{}개의 새로운 리소스 추가 완료", newResources.size());
        } else {
            log.debug("새로운 리소스 없음");
        }
    }
}


