package com.yeoboya.lunch.api.v1.profile.repository;

import com.yeoboya.lunch.api.v1.member.domain.Member;

public interface MyRepositoryCustom {

    Member findMyInfo(String myId);

}
