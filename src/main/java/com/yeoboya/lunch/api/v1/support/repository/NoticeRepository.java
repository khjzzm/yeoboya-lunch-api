package com.yeoboya.lunch.api.v1.support.repository;

import com.yeoboya.lunch.api.v1.support.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
