package com.yeoboya.lunch.api.v1.file.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseEntity;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 📌 공지사항 첨부 이미지 파일 엔티티
 * - 공지사항 작성 시 업로드된 이미지 메타데이터 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_FILE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_ID", nullable = false)
    private Notice notice;

    @Column(nullable = false)
    private String originalFileName; // 원본 파일명

    @Column(nullable = false)
    private String fileName; // 저장된 파일명 (S3에 저장된 이름)

    @Column(nullable = false)
    private String filePath; // S3 경로

    @Column(nullable = false)
    private String extension; // 확장자

    @Column(nullable = false)
    private Long size; // 파일 크기

    // 파일 업로드 시각
    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column(nullable = false)
    private String mimeType; // MIME 타입

    @Column(nullable = false)
    private String imageUrl; // 이미지 접근 URL

    @Column
    private String thumbnailUrl; // 썸네일 이미지 URL

    @Column
    private String checksum; // 무결성 확인용 체크섬

    @Column(nullable = false)
    private String uploadedBy; // 업로더 ID (관리자 등)

    @Column(nullable = false)
    private Boolean isPublic = true; // 공개 여부 (기본 true)

    @Builder
    public NoticeFile(Notice notice, String originalFileName, String fileName, String filePath, String extension,
                      Long size, LocalDateTime uploadDate, String mimeType, String imageUrl, String thumbnailUrl, String checksum,
                      String uploadedBy, Boolean isPublic) {
        this.notice = notice;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.extension = extension;
        this.size = size;
        this.uploadDate = uploadDate;
        this.mimeType = mimeType;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.checksum = checksum;
        this.uploadedBy = uploadedBy;
        this.isPublic = isPublic != null ? isPublic : true;
    }
}