package com.yeoboya.lunch.api.v1.board.base.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BoardHashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_HASHTAG_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private AbstractBoard board;  // `AbstractBoard`로 변경하여 여러 게시판에 사용 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HASHTAG_ID")
    private HashTag hashTag;

    public static BoardHashTag createBoardHashTag(HashTag hashTag) {
        BoardHashTag boardHashTag = new BoardHashTag();
        boardHashTag.setHashTag(hashTag);
        return boardHashTag;
    }
}