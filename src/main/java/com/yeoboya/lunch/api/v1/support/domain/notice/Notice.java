package com.yeoboya.lunch.api.v1.support.domain.notice;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("NOTICE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends AbstractBoard {

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String author;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean pinned;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    public void addFile(NoticeFile file) {
        this.noticeFiles.add(file);
        file.setNotice(this);
    }

    public void removeNoticeFile(NoticeFile file) {
        this.noticeFiles.remove(file);
        file.setNotice(null);
    }

    public static Notice createNotice(NoticeRequest noticeRequest) {
        Notice notice = new Notice();
        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        notice.setCategory(noticeRequest.getCategory());
        notice.setAuthor(noticeRequest.getAuthor());
        notice.setPinned(noticeRequest.getPinned());
        notice.setStartDate(noticeRequest.getStartDate());
        notice.setEndDate(noticeRequest.getEndDate());
        notice.setAttachmentUrl(noticeRequest.getAttachmentUrl());
        notice.setStatus(noticeRequest.getStatus());
        notice.setViewCount(0);
        return notice;
    }
}