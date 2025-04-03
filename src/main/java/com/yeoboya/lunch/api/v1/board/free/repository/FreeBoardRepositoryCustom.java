package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreeBoardRepositoryCustom {

    Page<FreeBoardProjection> boardList(BoardSearchCondition boardSearchCondition, Pageable pageable);
}
