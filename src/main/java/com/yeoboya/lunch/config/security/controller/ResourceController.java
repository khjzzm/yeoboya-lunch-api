package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.annotation.AuthReload;
import com.yeoboya.lunch.config.security.controller.specification.ResourceApi;
import com.yeoboya.lunch.config.security.reqeust.RoleResourcesRequest;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import com.yeoboya.lunch.config.security.service.ResourcesService;
import com.yeoboya.lunch.config.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {

    private final ResourcesService resourcesService;
    private final SecurityResourceService securityResourceService;

    /**
     * 리소스조회
     * role_resource, role join
     */
    @GetMapping
    public ResponseEntity<Response.Body> resources(Pageable pageable){
        return resourcesService.fetchAllResources(pageable);
    }

    /**
     * 리소스 권한 추가 및 수정
     */
    @PostMapping
    @AuthReload
    @CacheEvict(value = "resourceList", allEntries = true)
    public ResponseEntity<Response.Body> updateRoleResources(@RequestBody RoleResourcesRequest roleResourcesRequest){
        return resourcesService.updateRoleResources(roleResourcesRequest);
    }

    /**
     * 리소스삭제
     */
    @AuthReload
    @DeleteMapping
    public ResponseEntity<Response.Body> deleteResource(){
        return null;
    }


    /**
     * JWT 토큰 관리 조회
     */
    @GetMapping("/token-ignore-url")
    public  ResponseEntity<Response.Body> findTokenIgnoreUrl(){
        return resourcesService.findTokenIgnoreUrl();
    }


    /**
     * JWT 토큰 무시 관리
     */
    @PostMapping("/token-ignore-url")
    public  ResponseEntity<Response.Body> saveTokenIgnoreUrl(@RequestBody TokenIgnoreUrlRequest tokenIgnoreUrlRequest){
        return resourcesService.saveTokenIgnoreUrl(tokenIgnoreUrlRequest);
    }

    /**
     * JWT 토큰 관리 삭제
     */
    @DeleteMapping("/token-ignore-url/{id}")
    public  ResponseEntity<Response.Body> deleteTokenIgnoreUrl(@PathVariable Long id){
        return resourcesService.deleteTokenIgnoreUrl(id);
    }
}

