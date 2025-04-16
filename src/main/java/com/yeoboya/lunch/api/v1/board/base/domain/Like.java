package com.yeoboya.lunch.api.v1.board.base.domain;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"BOARD_ID", "MEMBER_ID"})
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private AbstractBoard board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public static Like createLike(Member member, AbstractBoard board) {
        Like like = new Like();
        like.setMember(member);
        like.setBoard(board);
        return like;
    }

    public static Like createAnonymousLike(AnonymousBoard board) {
        Like like = new Like();
        like.setBoard(board);
        return like;
    }
}