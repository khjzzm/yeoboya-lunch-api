package com.yeoboya.lunch.api.v1.file.domain;

import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 📌 회원 프로필 이미지 도메인 엔티티
 * - 회원(Member)의 프로필 이미지를 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_PROFILE_FILE_ID", nullable = false)
    private Long id;

    // 원본 파일명 (사용자가 업로드한 파일명)
    @Column(nullable = false)
    private String originalFileName;

    // 저장된 파일명 (S3에 저장된 파일명, UUID 또는 날짜 기반으로 생성)
    @Column(nullable = false)
    private String fileName;

    // 파일이 저장된 S3 경로 (디렉토리 구조 포함)
    @Column(nullable = false)
    private String filePath;

    // S3에서 접근 가능한 파일 URL (Public URL)
    @Column(nullable = false)
    private String imageUrl;

    // 파일 확장자 (예: png, jpg, pdf)
    @Column(nullable = false)
    private String extension;

    // 파일 크기 (bytes)
    @Column(nullable = false)
    private Long size;

    // 기본 프로필 여부 (true: 기본 프로필, false: 사용자 지정 프로필)
    @Column(nullable = false)
    private Boolean isDefault;

    // 파일 MIME 타입 (예: image/png, application/pdf)
    @Column(nullable = false)
    private String mimeType;

    // 파일 업로드 시각
    @Column(nullable = false)
    private LocalDateTime uploadDate;

    // 파일을 업로드한 사용자 ID
    @Column(nullable = false)
    private String uploadedBy;

    // 파일이 공개 여부 (true: 공개, false: 비공개)
    @Column(nullable = false)
    private Boolean isPublic;

    // 썸네일 URL (이미지 파일의 경우 썸네일이 생성되면 저장)
    private String thumbnailUrl;

    // 파일 무결성을 검증하기 위한 체크섬 (SHA256)
    @Column(nullable = false)
    private String checksum;

    // 연결된 회원 (`Member`)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    /**
     * 📌 `MemberProfileFile` 생성자 (`ProfileUploadResponse` 데이터를 기반으로 객체 생성)
     */
    @Builder
    public MemberProfileFile(Member member, ProfileResponse profileResponse) {
        this.member = member;
        this.originalFileName = profileResponse.getOriginalFileName();
        this.fileName = profileResponse.getFileName();
        this.filePath = profileResponse.getFilePath();
        this.extension = profileResponse.getExtension();
        this.imageUrl = profileResponse.getImageUrl();
        this.size = profileResponse.getSize();
        this.isDefault = profileResponse.getIsDefault();
        this.mimeType = profileResponse.getMimeType();
        this.uploadDate = profileResponse.getUploadDate();
        this.uploadedBy = profileResponse.getUploadedBy();
        this.isPublic = profileResponse.getIsPublic();
        this.thumbnailUrl = profileResponse.getThumbnailUrl();
        this.checksum = profileResponse.getChecksum();
    }

    /**
     * 📌 회원(Member)과 프로필 이미지(MemberProfileFile) 간의 관계 설정
     */
    public void saveMember(Member member) {
        this.member = member;
        if (!member.getMemberProfileFiles().contains(this)) {
            member.getMemberProfileFiles().add(this);
        }
    }

}