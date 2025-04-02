package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResource;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicResourceService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ResourcesRepository resourcesRepository;
    private final RoleRepository roleRepository;

    /**
     * Spring Security 리소스(자원) 목록을 자동으로 스캔하여 DB에 반영한다.
     * 리소스가 수정이 발견되면 자동으로 추가/삭제 한다.
     */
    @Transactional
    public void syncResources() {
        // 1. 현재 DB에 저장된 리소스 조회
        List<Resource> existingResources = resourcesRepository.findAllWithRoleResources();
        Set<String> existingResourceKeys = existingResources.stream()
                .map(res ->
                        res.getResourceName() + "_" + (res.getHttpMethod() == null ? "ALL" : res.getHttpMethod()) + "_" + res.getResourceType()
                )
                .collect(Collectors.toSet());

        // 2. 현재 컨트롤러에서 제공하는 리소스 가져오기
        Set<String> detectedResourceKeys = new HashSet<>();
        List<Resource> newResources = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    PreAuthorize methodAnnotation = entry.getValue().getMethodAnnotation(PreAuthorize.class);
                    boolean hasPreAuthorize = methodAnnotation != null;

                    String role;
                    if (hasPreAuthorize) {
                        Pattern pattern = Pattern.compile("hasRole\\('([^']+)'\\)|hasAuthority\\('([^']+)'\\)");
                        Matcher matcher = pattern.matcher(methodAnnotation.value());
                        role = matcher.find() ? (matcher.group(1) != null ? matcher.group(1) : matcher.group(2)) : "";
                    } else {
                        role = "";
                    }

                    EnumSet<HttpMethod> httpMethods = entry.getKey().getMethodsCondition().getMethods().isEmpty()
                            ? EnumSet.noneOf(HttpMethod.class)
                            : entry.getKey().getMethodsCondition().getMethods().stream()
                            .map(method -> HttpMethod.valueOf(method.name()))
                            .collect(Collectors.toCollection(() -> EnumSet.noneOf(HttpMethod.class)));

                    return entry.getKey().getDirectPaths().stream()
                            .filter(url -> !url.equals("/error"))
                            .map(url -> new ResourceMapping(url, httpMethods, hasPreAuthorize, role));
                })
                .map(mapping -> {
                    String httpMethodsStr = mapping.methods.isEmpty() ? "ALL" : mapping.methods.stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));

                    String uniqueKey = mapping.url + "_" + httpMethodsStr + "_" + (mapping.hasPreAuthorize ? "ROLE" : "URL");
                    detectedResourceKeys.add(uniqueKey);

                    if (existingResourceKeys.contains(uniqueKey)) {
                        // 기존 리소스에도 RoleResource 없으면 추가
                        existingResources.stream()
                                .filter(r -> (r.getResourceName() + "_" +
                                        (r.getHttpMethod() == null ? "ALL" : r.getHttpMethod()) + "_" +
                                        r.getResourceType()).equals(uniqueKey))
                                .findFirst()
                                .ifPresent(resource -> {
                                    if (resource.getRoleResources() == null || resource.getRoleResources().isEmpty()) {
                                        String roleValue = mapping.hasPreAuthorize
                                                ? (!mapping.role.isEmpty() ? mapping.role : "GUEST") : "GUEST";

                                        Authority authority = Authority.valueOf("ROLE_" + roleValue);
                                        Role role = roleRepository.findByRole(authority);
                                        if (role != null) {
                                            Set<RoleResource> roleResources = resource.getRoleResources();
                                            roleResources.clear(); // orphan 처리 정상 작동
                                            roleResources.add(new RoleResource(resource, role));
                                            resourcesRepository.save(resource);
                                            log.info("RoleResource 보정: {}", uniqueKey);
                                        }
                                    }
                                });
                        return null;
                    }

                    //  신규 리소스 처리
                    Resource resource = Resource.builder()
                            .resourceName(mapping.url)
                            .resourceType(mapping.hasPreAuthorize ? "ROLE" : "URL")
                            .orderNum(999)
                            .httpMethod(httpMethodsStr)
                            .build();

                    String roleValue = !mapping.role.isEmpty() ? mapping.role : "GUEST";
                    Authority authority = Authority.valueOf("ROLE_" + roleValue);
                    Role role = roleRepository.findByRole(authority);
                    if (role != null) {
                        resource.setRoleResources(Collections.singleton(new RoleResource(resource, role)));
                    }

                    return resource;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 3. 삭제 대상 리소스
        List<Resource> deletedResources = existingResources.stream()
                .filter(resource ->
                        !detectedResourceKeys.contains(
                                resource.getResourceName() + "_" +
                                        (resource.getHttpMethod() == null ? "ALL" : resource.getHttpMethod()) + "_" +
                                        resource.getResourceType()
                        )
                )
                .collect(Collectors.toList());

        // 4. 저장/삭제
        if (!newResources.isEmpty()) {
            resourcesRepository.saveAll(newResources);
            log.info("✅ {}개의 새로운 리소스가 추가되었습니다.", newResources.size());
        }

        if (!deletedResources.isEmpty()) {
            resourcesRepository.deleteAll(deletedResources);
            log.info("❌ {}개의 삭제된 리소스를 제거했습니다.", deletedResources.size());
        }

        log.info("🔁 리소스 동기화 완료");
    }


    public static class ResourceMapping {
        public final String url;
        public final Set<HttpMethod> methods;
        public final boolean hasPreAuthorize;
        public final String role;

        public ResourceMapping(String url, EnumSet<HttpMethod> methods, boolean hasPreAuthorize, String role) {
            this.url = url;
            this.methods = methods;
            this.hasPreAuthorize = hasPreAuthorize;
            this.role = role;
        }
    }
}


