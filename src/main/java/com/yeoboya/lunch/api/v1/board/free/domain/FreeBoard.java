package com.yeoboya.lunch.api.v1.board.free.domain;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.board.free.request.BoardCreate;
import com.yeoboya.lunch.api.v1.file.domain.BoardFile;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
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

    private Date createDate;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<BoardHashTag> boardHashTag = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "freeBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardFile> boardFiles = new ArrayList<>();


    public static FreeBoard createBoard(Member member, BoardCreate boardCreate, List<BoardHashTag> boardHashtag) {
        FreeBoard freeBoard = new FreeBoard();
        freeBoard.setMember(member);
        freeBoard.setTitle(boardCreate.getTitle());
        freeBoard.setContent(boardCreate.getContent());
        freeBoard.setPin(boardCreate.getPin());
        freeBoard.setSecret(boardCreate.isSecret());
        freeBoard.setCreateDate(new Date());
        for (BoardHashTag boardHashTag : boardHashtag) {
            freeBoard.addBoardHashTag(boardHashTag);
        }
        return freeBoard;
    }

    public static FreeBoard createBoard(Member member, BoardCreate boardCreate, List<BoardHashTag> boardHashtag, BoardFile boardFile) {
        FreeBoard freeBoard = createBoard(member, boardCreate, boardHashtag);
        freeBoard.addFile(boardFile);
        return freeBoard;
    }

    private void addBoardHashTag(BoardHashTag boardHashTag) {
        this.boardHashTag.add(boardHashTag);
        boardHashTag.setBoard(this);
    }

    private void addFile(BoardFile boardFile) {
        this.boardFiles.add(boardFile);
        if (boardFile.getFreeBoard() != this) {
            boardFile.setFreeBoard(this);
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
