package com.yeoboya.lunch.api.v1.event.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.event.domain.Banner;
import com.yeoboya.lunch.api.v1.event.reqeust.BannerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "Banner", description = "배너 관리 API")
public interface BannerApi {

    @Operation(summary = "배너 생성", description = "새로운 배너를 생성합니다.")
    @PostMapping
    ResponseEntity<Response.Body> createBanner(@RequestPart(value = "file", required = false) MultipartFile file,
                                               @RequestPart("bannerRequest") @Valid BannerRequest bannerRequest);

    @Operation(summary = "배너 조회", description = "배너 목록을 조회합니다.")
    @GetMapping
    ResponseEntity<Response.Body> getBanners(@RequestParam(required = false) Banner.DisplayLocation displayLocation);

    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다.")
    @DeleteMapping("/{id}")
    ResponseEntity<Response.Body> deleteBanner(@PathVariable Long id);
}