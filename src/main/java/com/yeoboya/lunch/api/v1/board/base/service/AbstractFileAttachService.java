package com.yeoboya.lunch.api.v1.board.base.service;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.service.fetcher.BoardFetcher;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.file.domain.AbstractFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * 게시판 글 작성 시 본문 내 이미지 URL을 기반으로 파일-게시글 연관관계를 설정하는 공통 추상 클래스
 */
public abstract class AbstractFileAttachService<T extends AbstractFile> {

    /**
     * 본문 이미지 URL로 해당 파일들을 조회하는 로직 (게시판 별로 구현)
     */
    protected abstract List<T> findFilesByImageUrls(List<String> urls);

    /**
     * 게시글 ID로 게시글 객체 조회하는 fetcher (게시판 별로 구현)
     */
    protected abstract BoardFetcher<? extends AbstractBoard> getBoardFetcher();

    /**
     * 파일과 게시글의 연관관계를 설정하는 로직 (게시판 별로 구현)
     */
    protected abstract void bindFileToBoard(T file, AbstractBoard board, boolean isFirst);

    /**
     * 본문 content를 분석하여 이미지 URL로 연관된 파일들을 찾아 게시글과 연결
     */
    @Transactional
    public void attachFilesFromContent(String content, AbstractBoard board) {
        List<String> imageUrls = extractImageUrls(content);
        List<T> files = findFilesByImageUrls(imageUrls);

        IntStream.range(0, files.size()).forEach(i -> {
            T file = files.get(i);
            bindFileToBoard(file, board, i == 0);
        });
    }

    /**
     * HTML content 내 img 태그의 src 속성에서 이미지 URL 추출
     */
    protected List<String> extractImageUrls(String htmlContent) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"'>]+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}