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

        // 1. 현재 DB에 저장된 리소스 조회
        List<Resources> existingResources = resourcesRepository.findAll();
        Set<String> existingResourceKeys = existingResources.stream()
                .map(res -> res.getResourceName() + "_" + (res.getHttpMethod() == null ? "ALL" : res.getHttpMethod()))
                .collect(Collectors.toSet());

        // 확인용 출력
        requestMappingHandlerMapping.getHandlerMethods().forEach((info, method) -> {
            log.warn("🔍 발견된 리소스: {} | HTTP 메서드: {}", info.getDirectPaths(), info.getMethodsCondition().getMethods());
        });

        // 2. 현재 컨트롤러에서 제공하는 리소스 가져오기
        Set<String> detectedResourceKeys = new HashSet<>();
        List<Resources> newResources = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> entry.getKey().getDirectPaths().stream()
                        .map(url -> new AbstractMap.SimpleEntry<>(url, entry.getKey().getMethodsCondition().getMethods()))
                )
                .map(entry -> {
                    String httpMethods = entry.getValue().isEmpty() ? "ALL" : entry.getValue().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));

                    String uniqueKey = entry.getKey() + "_" + httpMethods;
                    detectedResourceKeys.add(uniqueKey); // 발견된 리소스 저장

                    if (existingResourceKeys.contains(uniqueKey)) {
                        return null; // 기존에 존재하면 추가 안 함
                    }

                    return Resources.builder()
                            .resourceName(entry.getKey())
                            .resourceType("url")
                            .orderNum(999) // 기본 orderNum
                            .httpMethod(httpMethods)
                            .build();
                })
                .filter(Objects::nonNull) // null 제거
                .collect(Collectors.toList());


        // 3. 삭제해야 할 리소스 찾기 (DB에는 있지만, 현재 컨트롤러에 없는 것)
        List<Resources> deletedResources = existingResources.stream()
                .filter(resource -> !detectedResourceKeys.contains(resource.getResourceName() + "_" +
                        (resource.getHttpMethod() == null ? "ALL" : resource.getHttpMethod())))
                .collect(Collectors.toList());

        // 4. DB 반영 (추가 및 삭제)
        if (!newResources.isEmpty()) {
            resourcesRepository.saveAll(newResources);
            log.info("✅ {}개의 새로운 리소스가 추가되었습니다.", newResources.size());
        }
        if (!deletedResources.isEmpty()) {
            resourcesRepository.deleteAll(deletedResources);
            log.info("❌ {}개의 삭제된 리소스를 DB에서 제거했습니다.", deletedResources.size());
        }

        log.warn("✅ 동적 리소스 동기화 완료!");
    }
}


