package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.BoardHashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractHashTagService<T extends AbstractBoard> {

    protected final HashTagRepository hashTagRepository;
    protected final BoardHashTagRepository boardHashTagRepository;
    protected final BoardFetcher<T> boardFetcher;

    @Transactional
    public void saveHashtags(List<String> tags, Long boardId) {
        T board = boardFetcher.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + boardId));

        List<BoardHashTag> boardHashTags = Optional.ofNullable(tags)
                .orElse(Collections.emptyList())
                .stream()
                .map(tag -> {
                    HashTag hashTag = hashTagRepository.existsHashTagByTag(tag)
                            ? hashTagRepository.findHashTagByTag(tag)
                            : hashTagRepository.save(HashTag.builder().tag(tag).build());

                    BoardHashTag boardHashTag = BoardHashTag.createBoardHashTag(hashTag);
                    boardHashTag.setBoard(board);
                    return boardHashTag;
                })
                .collect(Collectors.toList());

        boardHashTagRepository.saveAll(boardHashTags);
    }

    public List<String> getHashTags(Long boardId) {
        return boardHashTagRepository.findByBoardId(boardId)
                .stream()
                .map(boardHashTag -> boardHashTag.getHashTag().getTag())
                .collect(Collectors.toList());
    }
}