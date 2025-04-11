package com.yeoboya.lunch.api.v1.board.free.domain;

import com.yeoboya.lunch.api.v1.board.base.domain.*;
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
public class FreeBoard extends AbstractBoard implements PinSupport {

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Member.class)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(nullable = true)
    private String pin;

    private boolean secret;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardHashTag> boardHashTag = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "freeBoard", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<FreeBoardFile> freeBoardFiles = new ArrayList<>();

    @Override
    public String getPin() {
        return this.pin;
    }

    public static FreeBoard createBoard(Member member, FreeBoardCreate freeBoardCreate, Category category, List<BoardHashTag> boardHashtag) {
        FreeBoard freeBoard = new FreeBoard();
        freeBoard.setMember(member);
        freeBoard.setContent(freeBoardCreate.getContent());
        freeBoard.setCategory(category);
        freeBoard.setTitle(freeBoardCreate.getTitle());
        freeBoard.setPin(freeBoardCreate.getPin());
        freeBoard.setSecret(freeBoardCreate.isSecret());
        for (BoardHashTag boardHashTag : boardHashtag) {
            freeBoard.addBoardHashTag(boardHashTag);
        }
        return freeBoard;
    }

    public void addFile(FreeBoardFile file) {
        this.freeBoardFiles.add(file);
        if (file.getFreeBoard() != this) {
            file.setFreeBoard(this);
        }
    }

    public void clearBoardHashTags() {
        for (BoardHashTag tag : new ArrayList<>(this.boardHashTag)) {
            tag.setBoard(null); // 반대쪽도 해제
        }
        this.boardHashTag.clear();
    }

    public void addBoardHashTag(BoardHashTag boardHashTag) {
        this.boardHashTag.add(boardHashTag);
        boardHashTag.setBoard(this);
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
