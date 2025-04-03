package com.yeoboya.lunch.api.v1.support.repository.notice;

import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
