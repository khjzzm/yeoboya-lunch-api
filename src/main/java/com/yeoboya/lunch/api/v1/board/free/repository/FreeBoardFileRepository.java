package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeBoardFileRepository extends JpaRepository<FreeBoardFile, Long> {
    List<FreeBoardFile> findByImageUrlIn(List<String> imageUrls);
}
