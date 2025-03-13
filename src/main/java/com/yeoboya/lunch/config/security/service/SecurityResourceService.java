package com.yeoboya.lunch.config.security.service;


import com.yeoboya.lunch.config.security.domain.AccessIp;
import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.matcher.CustomRequestMatcher;
import com.yeoboya.lunch.config.security.repository.AccessIpRepository;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import com.yeoboya.lunch.config.security.repository.TokenIgnoreUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 보안 리소스 관련 서비스를 제공하는 클래스.
 * 역할 기반의 접근 제어, IP 기반 접근 제한, 토큰을 무시할 URL 여부를 관리한다.
 */
@Slf4j
@Service
public class SecurityResourceService {

    private final DynamicResourceService dynamicResourceService;
    private final ResourcesRepository resourcesRepository;
    private final AccessIpRepository accessIpRepository;
    private final TokenIgnoreUrlRepository tokenIgnoreUrlRepository;

    /**
     * SecurityResourceService의 생성자.
     * 필요한 리포지토리를 주입받아 보안 관련 데이터를 관리한다.
     *
     * @param dynamicResourceService   새로운 리소스를 추가해주는 서비스
     * @param resourcesRepository      역할과 연관된 리소스를 관리하는 리포지토리
     * @param accessIpRepository       접근을 허용할 IP 목록을 관리하는 리포지토리
     * @param tokenIgnoreUrlRepository 인증 없이 접근 가능한 URL 목록을 관리하는 리포지토리
     */
    public SecurityResourceService(DynamicResourceService dynamicResourceService, ResourcesRepository resourcesRepository, AccessIpRepository accessIpRepository, TokenIgnoreUrlRepository tokenIgnoreUrlRepository) {
        this.dynamicResourceService = dynamicResourceService;
        this.resourcesRepository = resourcesRepository;
        this.accessIpRepository = accessIpRepository;
        this.tokenIgnoreUrlRepository = tokenIgnoreUrlRepository;
    }

    /**
     * 리소스(자원)별 접근 가능한 역할(Role) 정보를 반환한다.
     * 결과값은 캐싱하여 성능을 최적화한다.
     *
     * @return RequestMatcher와 역할 목록(ConfigAttribute)의 맵핑 정보 (리소스별 접근 가능한 역할 설정)
     */
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        // 자동 동기화 실행 (새로운 리소스가 DB에 없으면 추가)
        dynamicResourceService.syncResources();

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resource> resourceList = resourcesRepository.findAllResourcesWithRoles();

        if (resourceList == null || resourceList.isEmpty()) {
            log.error("No resources found in the database!");
            return result; // 빈 맵 반환하여 NPE 방지
        }

        resourceList.forEach(re -> {
            List<ConfigAttribute> configAttributeList = new ArrayList<>();

            re.getRoleResources().forEach(roleResource -> {
                Role role = roleResource.getRole();
                configAttributeList.add(new SecurityConfig(role.getRole().name()));

                // ✅ HTTP 메서드 구분 추가
                // String → EnumSet<HttpMethod> 변환 로직
                EnumSet<HttpMethod> httpMethods = (re.getHttpMethod() == null || re.getHttpMethod().isEmpty())
                        ? EnumSet.noneOf(HttpMethod.class) // ✅ HTTP 메서드가 없으면 EMPTY 설정
                        : Arrays.stream(re.getHttpMethod().split(","))
                        .map(String::trim)
                        .map(method -> {
                            try {
                                return HttpMethod.valueOf(method);
                            } catch (IllegalArgumentException e) {
                                return null; // 잘못된 값이면 무시
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(HttpMethod.class)));

                // ✅ URL + HTTP 메서드 조합으로 RequestMatcher 생성
                RequestMatcher matcher = new CustomRequestMatcher(re.getResourceName(), httpMethods);
                result.put(matcher, configAttributeList);
            });
        });

        log.error("{}", result);
        return result;
    }

    /**
     * 접근을 허용하는 IP 목록을 반환한다.
     * 결과값은 캐싱하여 성능을 최적화한다.
     *
     * @return 허용된 IP 목록
     */
//    @Cacheable(value = "accessIpList")
    public List<AccessIp> getAccessIpList() {
        return accessIpRepository.findAll(); // DB에서 모든 허용 IP 조회
    }

    /**
     * 특정 URL이 인증이 필요한지 여부를 판단한다.
     * 미리 정의된 토큰 무시(URL) 목록과 비교하여 일치하는 경우 인증을 무시한다.
     *
     * @param url 확인할 URL
     * @return 인증을 무시해야 하는 URL이면 true, 그렇지 않으면 false
     */
    public boolean shouldIgnore(String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        String uri = URI.create(url).getPath(); // URL에서 경로(path) 부분만 추출

        return tokenIgnoreUrlRepository.getTokenIgnoreUrls()
                .stream()
                .anyMatch(r ->
                        matcher.match(r.getUrl(), uri) && Boolean.TRUE.equals(r.getIsIgnore())
                ); // URL 패턴과 일치하고 인증 무시 설정이 true이면 인증을 무시
    }
}