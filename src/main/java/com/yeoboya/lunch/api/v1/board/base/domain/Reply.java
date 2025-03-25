package com.yeoboya.lunch.api.v1.board.base.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseEntity;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private AbstractBoard board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_REPLY_ID")
    private Reply parentReply;

    @Column(nullable = false)
    private String content;

    private Date createDate;

    public static Reply createComment(Member member, AbstractBoard board, String content, Reply parentReply) {
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setBoard(board);
        reply.setContent(content);
        reply.setCreateDate(new Date());
        reply.setParentReply(parentReply);
        return reply;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", post=" + board +
                ", member=" + member +
                ", parentReply=" + (parentReply != null ? parentReply.getId() : "None") +
                ", content='" + content + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}