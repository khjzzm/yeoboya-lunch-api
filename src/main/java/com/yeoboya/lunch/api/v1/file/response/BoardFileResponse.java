package com.yeoboya.lunch.api.v1.file.response;

import com.yeoboya.lunch.api.v1.file.domain.BoardFile;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 📌 게시판 이미지 업로드 응답 VO (BoardFile File Response)
 * - 게시판 이미지를 저장하고 응답할 때 사용
 */
@Getter
@Setter
public class BoardFileResponse extends FileResponse {

    /**
     * 📌 `BoardFile` 엔티티를 `FileUploadResponse`로 변환하는 메서드
     */
    public static FileResponse from(BoardFile files) {
        return FileResponse.builder()
                .originalFileName(files.getOriginalFileName())
                .fileName(files.getFileName())
                .filePath(files.getFilePath())
                .extension(files.getExtension())
                .size(files.getSize())
                .imageUrl("https://yeoboya-lunch-s3-bucket.s3.ap-northeast-2.amazonaws.com/" + files.getFilePath() + "/" + files.getFileName())
                .mimeType(files.getMimeType())
                .uploadDate(files.getUploadDate())
                .uploadedBy(files.getUploadedBy())
                .isPublic(files.getIsPublic())
                .thumbnailUrl(files.getThumbnailUrl())
                .checksum(files.getChecksum())
                .build();
    }


    public static BoardFileResponse apply(FileResponse fileResponse) {
        BoardFileResponse response = new BoardFileResponse();

        response.setOriginalFileName(fileResponse.getOriginalFileName());
        response.setFileName(fileResponse.getFileName());
        response.setFilePath(fileResponse.getFilePath());
        response.setExtension(fileResponse.getExtension());
        response.setImageUrl(fileResponse.getImageUrl());
        response.setSize(fileResponse.getSize());
        response.setMimeType(fileResponse.getMimeType());
        response.setUploadDate(fileResponse.getUploadDate());
        response.setUploadedBy(fileResponse.getUploadedBy());
        response.setIsPublic(fileResponse.getIsPublic());
        response.setThumbnailUrl(fileResponse.getThumbnailUrl());
        response.setChecksum(fileResponse.getChecksum());

        return response;
    }


}