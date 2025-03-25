package com.yeoboya.lunch.api.v1.support.domain;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
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

    @Builder
    public Notice(String title, String content, String category, String author, int priority,
                  LocalDateTime startDate, LocalDateTime endDate, String attachmentUrl,
                  int viewCount, NoticeStatus status) {
        this.setTitle(title);
        this.setContent(content);
        this.setViewCount(viewCount);
        this.category = category;
        this.author = author;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attachmentUrl = attachmentUrl;
        this.status = status;
    }
}