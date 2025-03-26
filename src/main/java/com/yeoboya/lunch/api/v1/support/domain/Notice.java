package com.yeoboya.lunch.api.v1.support.domain;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private int priority;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    public void addNoticeFile(NoticeFile file) {
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
        notice.setPriority(noticeRequest.getPriority().ordinal());
        notice.setStartDate(noticeRequest.getStartDate());
        notice.setEndDate(noticeRequest.getEndDate());
        notice.setAttachmentUrl(noticeRequest.getAttachmentUrl());
        notice.setStatus(noticeRequest.getStatus());
        notice.setViewCount(0);
        return notice;
    }
}