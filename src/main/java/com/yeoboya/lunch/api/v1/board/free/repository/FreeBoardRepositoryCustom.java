package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreeBoardRepositoryCustom {

    Page<FreeBoard> boardList(BoardSearch boardSearch, Pageable pageable);
}
