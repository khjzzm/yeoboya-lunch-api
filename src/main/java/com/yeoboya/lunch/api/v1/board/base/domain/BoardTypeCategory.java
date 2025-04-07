package com.yeoboya.lunch.api.v1.board.base.domain;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BoardTypeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_TYPE_CATEGORY_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false, length = 30)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static BoardTypeCategory of(BoardType boardType, Category category) {
        BoardTypeCategory btc = new BoardTypeCategory();
        btc.setBoardType(boardType);
        btc.setCategory(category);
        return btc;
    }
}