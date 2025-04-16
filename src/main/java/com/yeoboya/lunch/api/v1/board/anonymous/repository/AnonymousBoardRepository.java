package com.yeoboya.lunch.api.v1.board.anonymous.repository;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonymousBoardRepository extends JpaRepository<AnonymousBoard, Long>, AnonymousBoardRepositoryCustom {

    Optional<AnonymousBoard> findTopByOrderByIdDesc();
}