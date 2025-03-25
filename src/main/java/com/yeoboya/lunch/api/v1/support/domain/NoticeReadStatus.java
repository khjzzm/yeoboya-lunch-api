package com.yeoboya.lunch.api.v1.support.domain;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_READ_STATUS_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_ID", nullable = false)
    private Notice notice;

    @Column
    private LocalDateTime readAt; // null이면 안 읽은 상태

    @Builder
    public NoticeReadStatus(Member member, Notice notice, LocalDateTime readAt) {
        this.member = member;
        this.notice = notice;
        this.readAt = readAt;
    }
}