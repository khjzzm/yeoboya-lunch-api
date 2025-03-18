package com.yeoboya.lunch.api.v1.file.service;

import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.function.Function;

public interface FileService {

    <T extends FileResponse> T upload(MultipartFile multipartFile, Directory fileType, Function<FileResponse, T> mapper) throws IOException;
    String createDirectory(Directory subDirector);
    String generateFileName(String fileExtension);
    boolean isValidExtension(String filenameExtension);
}

