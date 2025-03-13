package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicResourceService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ResourcesRepository resourcesRepository;

    /**
     * Spring Security 리소스(자원) 목록을 자동으로 스캔하여 DB에 반영한다.
     * 리소스가 수정이 발견되면 자동으로 추가/삭제 한다.
     */
    @Transactional
    public void syncResources() {
        // 1. 현재 DB에 저장된 리소스 조회
        List<Resource> existingResources = resourcesRepository.findAll();
        Set<String> existingResourceKeys = existingResources.stream()
                .map(res ->
                        res.getResourceName() + "_" + (res.getHttpMethod() == null ? "ALL" : res.getHttpMethod()) + "_" + res.getResourceType()
                )
                .collect(Collectors.toSet());

        existingResourceKeys.forEach(System.out::println);

        // 확인용 출력
        requestMappingHandlerMapping.getHandlerMethods().forEach((info, method) -> {
            PreAuthorize preAuthorize = method.getMethodAnnotation(PreAuthorize.class);
            log.info("🔍 발견된 리소스: {} | HTTP 메서드: {} | PreAuthorize: {}",
                    info.getDirectPaths(),
                    info.getMethodsCondition().getMethods(),
                    preAuthorize != null ? preAuthorize.value() : "없음"
            );
        });

        // 2. 현재 컨트롤러에서 제공하는 리소스 가져오기
        Set<String> detectedResourceKeys = new HashSet<>();

        List<Resource> newResources = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    // Controller 메서드에서 @PreAuthorize 어노테이션 여부 확인
                    boolean hasPreAuthorize = entry.getValue().getMethodAnnotation(PreAuthorize.class) != null;

                    // HTTP 메서드 가져오기 (비어 있으면 EnumSet.noneOf)
                    EnumSet<HttpMethod> httpMethods = entry.getKey().getMethodsCondition().getMethods().isEmpty()
                            ? EnumSet.noneOf(HttpMethod.class)
                            : entry.getKey().getMethodsCondition().getMethods().stream()
                            .map(method -> HttpMethod.valueOf(method.name()))
                            .collect(Collectors.toCollection(() -> EnumSet.noneOf(HttpMethod.class)));

                    return entry.getKey().getDirectPaths().stream()
                            .filter(url -> !url.equals("/error")) // ✅ "/error" 엔드포인트 제외
                            .map(url -> new ResourceMapping(url, httpMethods, hasPreAuthorize));
                })
                .map(mapping -> {
                    // HTTP 메서드 집합을 문자열로 변환. 비어 있으면 "ALL" 사용
                    String httpMethodsStr = mapping.methods.isEmpty()
                            ? "ALL"
                            : mapping.methods.stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));

                    // 고유 키 생성 (URL + "_" + HTTP 메서드 문자열)
                    String uniqueKey = mapping.url + "_" + httpMethodsStr + "_" + (mapping.hasPreAuthorize ? "ROLE" : "URL");
                    log.error("{}", uniqueKey);
                    detectedResourceKeys.add(uniqueKey);

                    // preAuthorize가 있으면 resourceType은 "ROLE", 없으면 "URL"
                    String resourceType = mapping.hasPreAuthorize ? "ROLE" : "URL";

                    if (existingResourceKeys.contains(uniqueKey)) {
                        return null; // 이미 존재하면 추가 안 함
                    }

                    return Resource.builder()
                            .resourceName(mapping.url)
                            .resourceType(resourceType)
                            .orderNum(999) // 기본 orderNum
                            .httpMethod(httpMethodsStr)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        // 3. 삭제해야 할 리소스 찾기 (DB에는 있지만, 현재 컨트롤러에 없는 것)
        List<Resource> deletedResources = existingResources.stream()
                .filter(resource ->
                        !detectedResourceKeys.contains(
                                resource.getResourceName() + "_" +
                                (resource.getHttpMethod() == null ? "ALL" : resource.getHttpMethod()) + "_" +
                                resource.getResourceType()
                        )
                )
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

        log.debug("✅ 동적 리소스 동기화 완료!");
    }


    public class ResourceMapping {
        public final String url;
        public final Set<HttpMethod> methods;
        public final boolean hasPreAuthorize;

        public ResourceMapping(String url, EnumSet<HttpMethod> methods, boolean hasPreAuthorize) {
            this.url = url;
            this.methods = methods;
            this.hasPreAuthorize = hasPreAuthorize;
        }
    }
}


