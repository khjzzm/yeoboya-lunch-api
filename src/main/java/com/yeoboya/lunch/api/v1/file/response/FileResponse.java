package com.yeoboya.lunch.api.v1.file.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 파일 업로드 응답 VO (Value Object)
 * S3에 업로드된 파일의 정보를 담는 응답 객체
 */
@Getter
@Setter
@NoArgsConstructor
public class FileResponse {

    private String originalFileName;        //원본 파일명 (사용자가 업로드한 파일명)
    private String fileName;                //저장된 파일명 (S3에 저장된 파일명, UUID 또는 날짜 기반으로 생성)
    private String filePath;                //파일이 저장된 S3 경로 (디렉토리 구조 포함)
    private String extension;               //파일 확장자 (예: png, jpg, pdf)
    private String imageUrl;                //S3에서 접근 가능한 파일 URL (Public URL)
    private Long size;                      //파일 크기 (bytes)
    private String mimeType;                //파일 MIME 타입 (예: image/png, application/pdf)
    private LocalDateTime uploadDate;       //파일 업로드 시각
    private String uploadedBy;              //파일을 업로드한 사용자 ID
    private Boolean isPublic;               //파일이 공개 여부 (true: 공개, false: 비공개)
    private String thumbnailUrl;            //썸네일 URL (이미지 파일의 경우 썸네일이 생성되면 저장)
    private String checksum;                //파일 무결성을 검증하기 위한 체크섬 (SHA256)


    @Builder
    public FileResponse(String originalFileName, String fileName, String filePath, String extension, String imageUrl, Long size, String mimeType, LocalDateTime uploadDate, String uploadedBy, Boolean isPublic, String thumbnailUrl, String checksum) {
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.extension = extension;
        this.imageUrl = imageUrl;
        this.size = size;
        this.mimeType = mimeType;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
        this.isPublic = isPublic;
        this.thumbnailUrl = thumbnailUrl;
        this.checksum = checksum;
    }

}