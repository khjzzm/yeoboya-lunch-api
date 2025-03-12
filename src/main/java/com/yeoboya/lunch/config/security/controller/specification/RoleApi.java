package com.yeoboya.lunch.config.security.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.annotation.AuthReload;
import com.yeoboya.lunch.config.security.reqeust.AuthorityRequest;
import com.yeoboya.lunch.config.security.reqeust.SecurityRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Hidden

@Tag(name = "Role", description = "권한 및 보안 설정 관련 API")
public interface RoleApi {

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "권한 추가/수정", description = "회원의 권한을 추가하거나 수정합니다.")
    @PostMapping("/authority-update")
    ResponseEntity<Response.Body> updateAuthority(@RequestBody @Valid AuthorityRequest authorityRequest);

    @Operation(summary = "회원 권한 리스트 조회", description = "회원의 권한 리스트를 조회합니다.")
    @GetMapping("/authorities")
    ResponseEntity<Response.Body> getAuthorityList(Pageable pageable);

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "계정 잠금 상태 수정", description = "회원 계정의 잠금 상태를 변경합니다.")
    @PostMapping("/security-update")
    ResponseEntity<Response.Body> updateSecurity(@RequestBody @Valid SecurityRequest securityRequest);
}