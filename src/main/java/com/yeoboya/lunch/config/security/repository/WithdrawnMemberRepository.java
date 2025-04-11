// WithdrawnMemberRepository.java
package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.WithdrawnMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawnMemberRepository extends JpaRepository<WithdrawnMember, Long> {
}