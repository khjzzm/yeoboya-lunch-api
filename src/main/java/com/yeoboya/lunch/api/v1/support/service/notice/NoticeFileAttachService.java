package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.service.AbstractFileAttachService;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeFile;
import com.yeoboya.lunch.api.v1.support.repository.notice.NoticeFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeFileAttachService extends AbstractFileAttachService<NoticeFile> {

    private final NoticeFileRepository noticeFileRepository;
    private final BoardFetcher<Notice> boardFetcher;

    @Override
    protected List<NoticeFile> findFilesByImageUrls(List<String> urls) {
        return noticeFileRepository.findByImageUrlIn(urls);
    }

    @Override
    protected BoardFetcher<? extends AbstractBoard> getBoardFetcher() {
        return boardFetcher;
    }

    @Override
    protected void bindFileToBoard(NoticeFile file, AbstractBoard board, boolean isFirst) {
        Notice notice = (Notice) board;
        file.setIsThumbnail(isFirst);
        file.setUsedInContent(true);
        file.setNotice(notice);
        notice.addFile(file);
    }
}