package com.yeoboya.lunch.api.v1.support.repository;

import com.yeoboya.lunch.api.v1.support.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findByNotice(Notice notice);
    List<NoticeFile> findByImageUrlIn(List<String> imageUrls);
}