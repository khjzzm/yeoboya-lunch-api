package com.yeoboya.lunch.api.v1.board.free.domain;

import com.yeoboya.lunch.api.v1.file.domain.AbstractFile;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@DiscriminatorValue("FREE_BOARD_FILE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class FreeBoardFile extends AbstractFile {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FREE_BOARD_ID", nullable = true)
    private FreeBoard freeBoard;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Column(nullable = false)
    private Boolean usedInContent;

    public static FreeBoardFile from(FileResponse fileResponse) {
        return FreeBoardFile.builder()
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
