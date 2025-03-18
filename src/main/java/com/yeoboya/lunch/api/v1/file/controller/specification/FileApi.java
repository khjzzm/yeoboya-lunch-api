package com.yeoboya.lunch.api.v1.file.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "File", description = "파일 업로드 및 조회 API")
public interface FileApi {

    @Operation(summary = "AWS S3 파일 업로드", description = "AWS S3에 파일을 업로드합니다.")
    @PostMapping("/s3-upload")
    ResponseEntity<Response.Body> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Directory subDirectory) throws IOException;

    @Operation(summary = "AWS S3 파일 조회", description = "AWS S3에 업로드된 파일 목록을 조회합니다.")
    @GetMapping("/s3")
    ResponseEntity<Response.Body> readFile();
}