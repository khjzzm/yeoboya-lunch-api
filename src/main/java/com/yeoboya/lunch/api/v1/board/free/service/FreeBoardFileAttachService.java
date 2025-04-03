package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractFileAttachService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoardFile;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeBoardFileAttachService extends AbstractFileAttachService<FreeBoardFile> {

    private final FreeBoardFileRepository freeBoardFileRepository;
    private final BoardFetcher<FreeBoard> boardFetcher;

    @Override
    protected List<FreeBoardFile> findFilesByImageUrls(List<String> urls) {
        return freeBoardFileRepository.findByImageUrlIn(urls);
    }

    @Override
    protected BoardFetcher<? extends AbstractBoard> getBoardFetcher() {
        return boardFetcher;
    }

    @Override
    protected void bindFileToBoard(FreeBoardFile file, AbstractBoard board, boolean isFirst) {
        FreeBoard freeBoard = (FreeBoard) board;
        file.setIsThumbnail(isFirst);
        file.setUsedInContent(true);
        file.setFreeBoard(freeBoard);
        freeBoard.addFile(file);
    }
}
