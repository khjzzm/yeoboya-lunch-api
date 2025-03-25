package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {

}
