package com.yeoboya.lunch.api.v1.file.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.controller.specification.FileApi;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController implements FileApi {

    private final Response response;
    private final FileServiceS3 fileServiceS3;

    /**
     * AWS 파일 업로드
     */
    @PostMapping("/s3-upload")
    public ResponseEntity<Body> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Directory subDirectory) {
        FileResponse upload = fileServiceS3.upload(file, subDirectory, null);
        return response.success(Code.SAVE_SUCCESS, upload);
    }

    @GetMapping("/s3")
    public ResponseEntity<Body> readFile() {
        return response.success(Code.SAVE_SUCCESS, fileServiceS3.read());
    }
}
