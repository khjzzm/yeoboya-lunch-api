package com.yeoboya.lunch.api.v1.support.domain;

import com.yeoboya.lunch.api.v1.file.domain.AbstractFile;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 공지사항 첨부 이미지 파일 엔티티
 * - 공지사항 작성 시 업로드된 이미지 메타데이터 저장
 */
@Entity
@DiscriminatorValue("NOTICE_FILE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class NoticeFile extends AbstractFile {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_ID", nullable = true)
    private Notice notice;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Column(nullable = false)
    private Boolean usedInContent;

    public static NoticeFile from(FileResponse fileResponse) {
        return NoticeFile.builder()
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
                .isThumbnail(false)
                .usedInContent(false)
                .build();
    }
}