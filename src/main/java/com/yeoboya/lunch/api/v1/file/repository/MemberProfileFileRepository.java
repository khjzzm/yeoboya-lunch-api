package com.yeoboya.lunch.api.v1.file.repository;

import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface MemberProfileFileRepository extends JpaRepository<MemberProfileFile, Long> {

    @Query("SELECT mpf FROM MemberProfileFile mpf WHERE mpf.member.loginId = :loginId AND mpf.id = :id")
    Optional<MemberProfileFile> findByMemberLoginIdAndId(@Param("loginId") String memberLoginId, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member_profile_file SET is_default = false WHERE member_id = (SELECT member_id FROM member WHERE login_id = :loginId)", nativeQuery = true)
    void resetDefaultProfileImage(@Param("loginId") String loginId);

    /** 특정 회원의 최신 프로필 사진 1개 가져오기 (ID 기준 내림차순) */
    Optional<MemberProfileFile> findTopByMemberOrderByIdDesc(Member member);

}
