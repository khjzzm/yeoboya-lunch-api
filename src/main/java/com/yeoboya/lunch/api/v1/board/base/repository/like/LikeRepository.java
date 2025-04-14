package com.yeoboya.lunch.api.v1.board.base.repository.like;

import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.Like;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberLoginIdAndBoardId(String loginId, Long boardId);
    boolean existsByMemberAndBoard(Member member, AbstractBoard board);

    @Modifying
    @Query("UPDATE Like r SET r.member = :dummy WHERE r.member = :original")
    void updateMemberToDummy(@Param("original") Member original, @Param("dummy") Member dummy);
}
