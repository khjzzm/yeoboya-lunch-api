package com.yeoboya.lunch.api.v1.file.domain;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 📌 게시판 첨부 파일 도메인 엔티티
 * - 게시글(Board)과 연결된 파일 정보를 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_FILE_ID", nullable = false)
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

    // 연결된 게시글 (`Board`)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private FreeBoard freeBoard;


    public static BoardFile from(FileResponse fileResponse) {
        if (fileResponse == null) {
            return null;
        }

        BoardFile boardFile = new BoardFile();
        boardFile.setOriginalFileName(fileResponse.getOriginalFileName());
        boardFile.setFileName(fileResponse.getFileName());
        boardFile.setFilePath(fileResponse.getFilePath());
        boardFile.setImageUrl(fileResponse.getImageUrl());
        boardFile.setExtension(fileResponse.getExtension());
        boardFile.setSize(fileResponse.getSize());
        boardFile.setMimeType(fileResponse.getMimeType());
        boardFile.setUploadDate(fileResponse.getUploadDate());
        boardFile.setUploadedBy(fileResponse.getUploadedBy());
        boardFile.setIsPublic(fileResponse.getIsPublic());
        boardFile.setThumbnailUrl(fileResponse.getThumbnailUrl());
        boardFile.setChecksum(fileResponse.getChecksum());

        return boardFile;
    }

    /**
     * 게시글(Board)과 파일(BoardFile) 간의 관계 설정
     */
    public void setFreeBoard(FreeBoard freeBoard) {
        this.freeBoard = freeBoard;
        if (!freeBoard.getBoardFiles().contains(this)) {
            freeBoard.getBoardFiles().add(this);
        }
    }

}