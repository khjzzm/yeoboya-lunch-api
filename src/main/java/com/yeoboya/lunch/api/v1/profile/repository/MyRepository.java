package com.yeoboya.lunch.api.v1.profile.repository;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyRepository extends JpaRepository<Member, Long>, MyRepositoryCustom {

}
