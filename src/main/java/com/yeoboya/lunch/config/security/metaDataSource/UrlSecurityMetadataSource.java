package com.yeoboya.lunch.config.security.metaDataSource;

import com.yeoboya.lunch.config.security.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring Security의 MetadataSource로, 요청 URL별 권한을 동적으로 관리하는 핵심 클래스
 */
@Slf4j
@Service
public class UrlSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;
    private final SecurityResourceService securityResourceService;

    public UrlSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap, SecurityResourceService securityResourceService) {
        this.requestMap = requestMap;
        this.securityResourceService = securityResourceService;
    }

    /**
     * 요청된 URL에 대한 접근 제어 목록 반환
     * @param object 요청 정보 (FilterInvocation)
     * @return 요청 URL과 일치하는 권한 목록
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest httpServletRequest = fi.getHttpRequest();

        if (requestMap == null) {
            return null;
        }

        return requestMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(httpServletRequest)) // URL 매칭
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 모든 권한 리스트 반환
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return requestMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    /**
     * 지원하는 클래스인지 확인
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 권한 설정 정보를 런타임에서 다시 로드
     */
    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceService.getResourceList();
        requestMap.clear();
        requestMap.putAll(reloadedMap);
        log.warn("Secured Url Resources - Role Mappings reloaded at Runtime!");
    }
}