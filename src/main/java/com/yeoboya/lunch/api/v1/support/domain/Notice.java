package com.yeoboya.lunch.api.v1.support.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseEntity;
import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int priority;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String attachmentUrl;

    @Column(nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;

    @PrePersist
    protected void prePersist() {
        if (viewCount == 0) this.viewCount = 0;
    }

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeFile> noticeFiles = new ArrayList<>();


    public void addNoticeFile(NoticeFile file) {
        noticeFiles.add(file);
        file.setNotice(this); // 연관관계 주인 설정
    }

    public void removeNoticeFile(NoticeFile file) {
        noticeFiles.remove(file);
        file.setNotice(null);
    }

    @Builder
    public Notice(String title, String content, String category, String author, int priority, LocalDateTime startDate,
                  LocalDateTime endDate, String attachmentUrl, int viewCount, NoticeStatus status) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attachmentUrl = attachmentUrl;
        this.viewCount = viewCount;
        this.status = status;
    }
}
