package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {

}
