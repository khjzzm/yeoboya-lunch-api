package com.yeoboya.lunch.api.v1.board.base.repository.reply;

import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom {

    @Query("select count(r) from Reply r where r.board.id = :boardId")
    long countByBoardId(@Param("boardId") Long boardId);

    long countByBoard_Id(Long boardId);

    Page<Reply> findByBoardId(Long boardId, Pageable pageable);

    @Modifying
    @Query("UPDATE Reply r SET r.member = :dummy WHERE r.member = :withdrawn")
    void updateMemberToDummy(@Param("withdrawn") Member withdrawn, @Param("dummy") Member dummy);
}
