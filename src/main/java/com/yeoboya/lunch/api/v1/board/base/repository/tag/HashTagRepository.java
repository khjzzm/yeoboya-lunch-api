package com.yeoboya.lunch.api.v1.board.base.repository.tag;

import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    boolean existsHashTagByTag(String tag);
    Optional<HashTag> findHashTagByTag(String tag);

}
