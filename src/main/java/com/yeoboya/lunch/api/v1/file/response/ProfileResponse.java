package com.yeoboya.lunch.api.v1.file.response;

import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.units.qual.A;

/**
 * 📌 프로필 이미지 업로드 응답 VO (Profile File Response)
 * - 사용자 프로필 이미지를 저장하고 응답할 때 사용
 */
@Getter
@Setter
@SuperBuilder
public class ProfileResponse extends FileResponse {

    /**
     * 프로필 이미지 고유 번호 (DB의 ID)
     */
    private Long imageNo;

    /**
     * 기본 프로필 여부 (true: 기본 프로필, false: 사용자 지정 프로필)
     */
    private Boolean isDefault;


    /**
     * 📌 `MemberProfileFile` 엔티티를 `ProfileResponse`로 변환하는 메서드
     *
     * @param fileEntity 저장된 `MemberProfileFile` 엔티티
     * @return `ProfileResponse`
     */
    public static ProfileResponse from(MemberProfileFile fileEntity) {
        return ProfileResponse.builder()
                .imageNo(fileEntity.getId())
                .originalFileName(fileEntity.getOriginalFileName())
                .fileName(fileEntity.getFileName())
                .filePath(fileEntity.getFilePath())
                .extension(fileEntity.getExtension())
                .imageUrl(fileEntity.getImageUrl())
                .size(fileEntity.getSize())
                .mimeType(fileEntity.getMimeType())
                .uploadDate(fileEntity.getUploadDate())
                .uploadedBy(fileEntity.getUploadedBy())
                .isPublic(fileEntity.getIsPublic())
                .thumbnailUrl(fileEntity.getThumbnailUrl())
                .checksum(fileEntity.getChecksum())
                .isDefault(fileEntity.getIsDefault() != null ? fileEntity.getIsDefault() : false)
                .build();
    }


    public static ProfileResponse apply(FileResponse fileResponse) {
        return ProfileResponse.builder()
                .imageNo(fileResponse instanceof ProfileResponse ? ((ProfileResponse) fileResponse).getImageNo() : null)
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

                .isDefault(fileResponse instanceof ProfileResponse && ((ProfileResponse) fileResponse).getIsDefault() != null
                        ? ((ProfileResponse) fileResponse).getIsDefault()
                        : false)
                .build();
    }

}