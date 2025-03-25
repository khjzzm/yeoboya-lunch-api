package com.yeoboya.lunch.api.v1.file.response;

import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import lombok.Getter;
import lombok.Setter;

/**
 * 📌 공지사항 이미지 업로드 응답 VO (NoticeFile Response)
 * - 공지사항 작성 시 첨부된 이미지의 응답 데이터 구조
 */
@Getter
@Setter
public class NoticeFileResponse extends FileResponse {

    /**
     * 📌 `NoticeFile` 엔티티로부터 응답 객체 생성
     */
    public static NoticeFileResponse from(NoticeFile file) {
        NoticeFileResponse response = new NoticeFileResponse();

        response.setOriginalFileName(file.getOriginalFileName());
        response.setFileName(file.getFileName());
        response.setFilePath(file.getFilePath());
        response.setExtension(file.getExtension());
        response.setSize(file.getSize());
        response.setImageUrl("https://yeoboya-lunch-s3-bucket.s3.ap-northeast-2.amazonaws.com/" + file.getFilePath() + "/" + file.getFileName());
        response.setMimeType(file.getMimeType());
        response.setUploadDate(file.getUploadDate());
        response.setUploadedBy(file.getUploadedBy());
        response.setIsPublic(file.getIsPublic());
        response.setThumbnailUrl(file.getThumbnailUrl());
        response.setChecksum(file.getChecksum());

        return response;
    }

    /**
     * 📌 일반 FileResponse를 NoticeFileResponse로 변환
     */
    public static NoticeFileResponse apply(FileResponse fileResponse) {
        NoticeFileResponse response = new NoticeFileResponse();

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