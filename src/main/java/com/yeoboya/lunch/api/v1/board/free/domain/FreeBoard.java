package com.yeoboya.lunch.api.v1.board.free.domain;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.board.free.request.FreeBoardCreate;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("FREE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FreeBoard extends AbstractBoard {

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Member.class)
    @JoinColumn(name = "MEMBER_ID", updatable = false)
    private Member member;

    private int pin;

    private boolean secret;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<BoardHashTag> boardHashTag = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "freeBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FreeBoardFile> freeBoardFiles = new ArrayList<>();

    public static FreeBoard createBoard(Member member, FreeBoardCreate freeBoardCreate, Category category, List<BoardHashTag> boardHashtag) {
        FreeBoard freeBoard = new FreeBoard();
        freeBoard.setMember(member);
        freeBoard.setCategory(category);
        freeBoard.setTitle(freeBoardCreate.getTitle());
        freeBoard.setPin(freeBoardCreate.getPin());
        freeBoard.setSecret(freeBoardCreate.isSecret());
        for (BoardHashTag boardHashTag : boardHashtag) {
            freeBoard.addBoardHashTag(boardHashTag);
        }
        return freeBoard;
    }

    private void addBoardHashTag(BoardHashTag boardHashTag) {
        this.boardHashTag.add(boardHashTag);
        boardHashTag.setBoard(this);
    }

    public void addFile(FreeBoardFile file) {
        this.freeBoardFiles.add(file);
        if (file.getFreeBoard() != this) {
            file.setFreeBoard(this);
        }
    }

    public void addLike(Like like) {
        this.getLikes().add(like);
        like.setBoard(this);
    }

    public void removeLike(Like like) {
        this.getLikes().remove(like);
        like.setBoard(null);
    }
}
