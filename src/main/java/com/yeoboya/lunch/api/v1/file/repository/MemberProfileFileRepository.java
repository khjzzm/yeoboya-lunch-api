package com.yeoboya.lunch.api.v1.file.repository;

import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
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

//    @Query("SELECT mpf FROM MemberProfileFile mpf WHERE mpf.member.loginId = :loginId AND mpf.isDefault = true")
//    List<MemberProfileFile> findByMember_LoginIdAndIsDefaultTrue(@Param("loginId") String loginId);
}
