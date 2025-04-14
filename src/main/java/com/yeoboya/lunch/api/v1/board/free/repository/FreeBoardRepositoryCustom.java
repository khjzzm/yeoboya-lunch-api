package com.yeoboya.lunch.api.v1.board.free.repository;

import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardResponse;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FreeBoardRepositoryCustom {

    Page<FreeBoardResponse> boardList(BoardSearchCondition boardSearchCondition, Pageable pageable);

    void deleteAllByMember(Member member);

}
