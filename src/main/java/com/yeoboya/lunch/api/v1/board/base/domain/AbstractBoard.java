package com.yeoboya.lunch.api.v1.board.base.domain;

import com.yeoboya.lunch.api.v1.common.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // 혹은 SINGLE_TABLE, TABLE_PER_CLASS 선택 가능
@DiscriminatorColumn(name = "board_type") // 선택사항
@Getter
@Setter
public abstract class AbstractBoard extends BaseEntity {

    @Id
    @Column(name = "BOARD_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();


    public void addLike(Like like) {
        this.likes.add(like);
        like.setBoard(this);
    }

    public void removeLike(Like like) {
        this.likes.remove(like);
        like.setBoard(null);
    }

}