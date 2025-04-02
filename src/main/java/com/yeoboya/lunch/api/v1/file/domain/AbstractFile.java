package com.yeoboya.lunch.api.v1.file.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "file_type")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractFile extends BaseEntity {

    @Id
    @Column(name = "FILE_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

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

    @Column(nullable = false)
    private LocalDateTime uploadDate; // 파일 업로드 시각

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

}
