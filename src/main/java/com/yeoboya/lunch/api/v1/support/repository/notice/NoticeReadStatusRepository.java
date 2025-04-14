package com.yeoboya.lunch.api.v1.support.repository.notice;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeReadStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoticeReadStatusRepository extends JpaRepository<NoticeReadStatus, Long> {
    List<NoticeReadStatus> findByMember(Member member);
    Optional<NoticeReadStatus> findByMemberAndNotice(Member member, Notice notice);

    @Modifying
    @Query("delete from NoticeReadStatus n where n.member = :member")
    void deleteByMember(@Param("member") Member member);
}
