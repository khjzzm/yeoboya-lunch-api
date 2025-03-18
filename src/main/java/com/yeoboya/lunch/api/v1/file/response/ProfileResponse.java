package com.yeoboya.lunch.api.v1.file.response;

import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import lombok.*;

/**
 * 📌 프로필 이미지 업로드 응답 VO (Profile File Response)
 * - 사용자 프로필 이미지를 저장하고 응답할 때 사용
 */
@Getter
@Setter
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
        ProfileResponse response = new ProfileResponse();

        response.setImageNo(fileEntity.getId());
        response.setOriginalFileName(fileEntity.getOriginalFileName());
        response.setFileName(fileEntity.getFileName());
        response.setFilePath(fileEntity.getFilePath());
        response.setExtension(fileEntity.getExtension());
        response.setImageUrl(fileEntity.getImageUrl());
        response.setSize(fileEntity.getSize());
        response.setMimeType(fileEntity.getMimeType());
        response.setUploadDate(fileEntity.getUploadDate());
        response.setUploadedBy(fileEntity.getUploadedBy());
        response.setIsPublic(fileEntity.getIsPublic());
        response.setThumbnailUrl(fileEntity.getThumbnailUrl());
        response.setChecksum(fileEntity.getChecksum());

        // ✅ `isDefault` 값이 `null`이면 기본값 `false` 설정
        response.setIsDefault(fileEntity.getIsDefault() != null ? fileEntity.getIsDefault() : false);

        return response;
    }


    public static ProfileResponse apply(FileResponse fileResponse) {
        ProfileResponse response = new ProfileResponse();

        response.setImageNo(fileResponse instanceof ProfileResponse ? ((ProfileResponse) fileResponse).getImageNo() : null);
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

        // ✅ isDefault 설정 (null 체크 후 기본값 false)
        response.setIsDefault(fileResponse instanceof ProfileResponse && ((ProfileResponse) fileResponse).getIsDefault() != null
                ? ((ProfileResponse) fileResponse).getIsDefault()
                : false);

        return response;
    }

}