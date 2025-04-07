package com.yeoboya.lunch.api.v1.support.domain.notice;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import com.yeoboya.lunch.api.v1.support.request.NoticeCreate;
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

    public static Notice createNotice(NoticeCreate noticeCreate, Category category) {
        Notice notice = new Notice();
        notice.setTitle(noticeCreate.getTitle());
        notice.setContent(noticeCreate.getContent());
        notice.setCategory(category);
        notice.setAuthor(noticeCreate.getAuthor());
        notice.setPinned(noticeCreate.getPinned());
        notice.setStartDate(noticeCreate.getStartDate());
        notice.setEndDate(noticeCreate.getEndDate());
        notice.setAttachmentUrl(noticeCreate.getAttachmentUrl());
        notice.setStatus(noticeCreate.getStatus());
        notice.setViewCount(0);
        return notice;
    }
}