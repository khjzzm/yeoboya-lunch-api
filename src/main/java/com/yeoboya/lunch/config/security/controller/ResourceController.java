package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.controller.specification.ResourceApi;
import com.yeoboya.lunch.config.security.reqeust.RoleResourcesRequest;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import com.yeoboya.lunch.config.security.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {

    private final ResourceService resourceService;

    /**
     * 리소스조회
     * role_resource, role join
     */
    @GetMapping
    public ResponseEntity<Response.Body> resources(Pageable pageable){
        return resourceService.fetchAllResources(pageable);
    }

    /**
     * 리소스 권한 추가 및 수정
     */
    @PostMapping
    public ResponseEntity<Response.Body> updateRoleResources(@RequestBody RoleResourcesRequest roleResourcesRequest){
        return resourceService.updateRoleResources(roleResourcesRequest);
    }


    /**
     * JWT 토큰 관리 조회
     */
    @GetMapping("/token-ignore-url")
    public  ResponseEntity<Response.Body> findTokenIgnoreUrl(){
        return resourceService.findTokenIgnoreUrl();
    }


    /**
     * JWT 토큰 무시 관리
     */
    @PostMapping("/token-ignore-url")
    public  ResponseEntity<Response.Body> saveTokenIgnoreUrl(@RequestBody TokenIgnoreUrlRequest tokenIgnoreUrlRequest){
        return resourceService.saveTokenIgnoreUrl(tokenIgnoreUrlRequest);
    }

    /**
     * JWT 토큰 관리 삭제
     */
    @DeleteMapping("/token-ignore-url/{id}")
    public  ResponseEntity<Response.Body> deleteTokenIgnoreUrl(@PathVariable Long id){
        return resourceService.deleteTokenIgnoreUrl(id);
    }
}

