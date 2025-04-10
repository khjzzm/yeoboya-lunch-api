package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
}