package com.yeoboya.lunch.api.v1.board.base.repository.tag;

import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    boolean existsHashTagByTag(String tag);
    HashTag findHashTagByTag(String tag);

}
