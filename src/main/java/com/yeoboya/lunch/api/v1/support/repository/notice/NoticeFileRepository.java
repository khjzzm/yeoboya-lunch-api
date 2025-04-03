package com.yeoboya.lunch.api.v1.support.repository.notice;

import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeFile;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findByNotice(Notice notice);
    List<NoticeFile> findByImageUrlIn(List<String> imageUrls);
}