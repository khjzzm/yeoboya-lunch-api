package com.yeoboya.lunch.api.v1.board.base.repository.like;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberLoginIdAndBoardId(String loginId, Long boardId);
    boolean existsByMemberAndBoard(Member member, AbstractBoard board);
}
