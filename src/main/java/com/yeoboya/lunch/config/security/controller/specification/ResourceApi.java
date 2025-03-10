package com.yeoboya.lunch.config.security.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.reqeust.ResourcesRequest;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "Resource", description = "리소스 관리 API")
public interface ResourceApi {

    @Operation(summary = "리소스 추가", description = "새로운 리소스를 등록합니다.")
    @PostMapping("/add")
    ResponseEntity<Response.Body> addResource(@RequestBody ResourcesRequest resourcesRequest);

    @Operation(summary = "리소스 조회", description = "등록된 모든 리소스를 조회합니다.")
    @GetMapping
    ResponseEntity<Response.Body> resources(Pageable pageable);

    @Operation(summary = "리소스 삭제", description = "리소스를 삭제합니다.")
    @DeleteMapping
    ResponseEntity<Response.Body> deleteResource();

    @Operation(summary = "JWT 토큰 (URL) 조회", description = "JWT 토큰이 필요 없는 URL을 조회합니다.")
    @GetMapping("/token-ignore-url")
    ResponseEntity<Response.Body> findTokenIgnoreUrl();

    @Operation(summary = "JWT 토큰 (URL) 관리", description = "JWT 토큰이 필요 없는 URL을 관리합니다.")
    @PostMapping("/token-ignore-url")
    ResponseEntity<Response.Body> saveTokenIgnoreUrl(@RequestBody TokenIgnoreUrlRequest tokenIgnoreUrlRequest);

    @Operation(summary = "JWT 토큰 (URL) 관리", description = "JWT 토큰이 필요 없는 URL을 삭제합니다.")
    @DeleteMapping("/token-ignore-url/{id}")
    ResponseEntity<Response.Body> deleteTokenIgnoreUrl(@PathVariable Long id);
}