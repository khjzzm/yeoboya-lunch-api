package com.yeoboya.lunch.api.v1.board.base.repository.tag;

import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardHashTagRepository extends JpaRepository<BoardHashTag, Long> {

    // 게시글 ID 기준으로 해시태그 연결 정보 조회
    List<BoardHashTag> findByBoardId(Long boardId);
}
