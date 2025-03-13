package com.yeoboya.lunch.api.v1.member.repository;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.config.security.domain.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {


    Optional<Member> findByEmail(String email);

    Optional<Member> findByLoginId(String loginId);

    <T> T findByEmail(String email, Class<T> type);

    <T> T findByLoginId(String loginId, Class<T> type);

    boolean existsMemberByLoginId(String loginId);

    boolean existsMemberByEmail(String email);

    Optional<Member> findByEmailAndProvider(String email, String provider);

    boolean existsByEmailAndProvider(String email, String provider);

    boolean existsMemberByEmailAndMemberInfoPhoneNumber(String email, String phoneNumber);

}
