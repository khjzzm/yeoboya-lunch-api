package com.yeoboya.lunch.api.v1.file.response;

import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeFile;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 공지사항 이미지 업로드 응답 VO (NoticeFile Response)
 */
@Getter
@Setter
@SuperBuilder
public class NoticeFileResponse extends FileResponse {

    /**
     * `NoticeFile` 엔티티로부터 응답 객체 생성
     */
    public static NoticeFileResponse from(NoticeFile file) {
        return NoticeFileResponse.builder()
                .originalFileName(file.getOriginalFileName())
                .fileName(file.getFileName())
                .filePath(file.getFilePath())
                .extension(file.getExtension())
                .size(file.getSize())
                .imageUrl("https://yeoboya-lunch-s3-bucket.s3.ap-northeast-2.amazonaws.com/" + file.getFilePath() + "/" + file.getFileName())
                .mimeType(file.getMimeType())
                .uploadDate(file.getUploadDate())
                .uploadedBy(file.getUploadedBy())
                .isPublic(file.getIsPublic())
                .thumbnailUrl(file.getThumbnailUrl())
                .checksum(file.getChecksum())
                .build();
    }

    /**
     * 일반 FileResponse를 NoticeFileResponse로 변환
     */
    public static NoticeFileResponse apply(FileResponse fileResponse) {
        return NoticeFileResponse.builder()
                .originalFileName(fileResponse.getOriginalFileName())
                .fileName(fileResponse.getFileName())
                .filePath(fileResponse.getFilePath())
                .extension(fileResponse.getExtension())
                .imageUrl(fileResponse.getImageUrl())
                .size(fileResponse.getSize())
                .mimeType(fileResponse.getMimeType())
                .uploadDate(fileResponse.getUploadDate())
                .uploadedBy(fileResponse.getUploadedBy())
                .isPublic(fileResponse.getIsPublic())
                .thumbnailUrl(fileResponse.getThumbnailUrl())
                .checksum(fileResponse.getChecksum())
                .build();
    }
}