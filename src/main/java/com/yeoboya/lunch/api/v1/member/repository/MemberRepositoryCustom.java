package com.yeoboya.lunch.api.v1.member.repository;

import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.reqeust.SearchMember;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.api.v1.member.response.MemberRoleResponse;
import com.yeoboya.lunch.config.security.reqeust.SearchRoleMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    Page<MemberResponse> findMembersInPages(SearchMember searchMember, Pageable pageable);

    MemberInfo getMemberInfo(String loginId);

    MemberResponse memberProfile(String loginId);

    List<MemberProfileFile> profileImg(String loginId);

    Page<MemberRoleResponse> findWithRolesInPages(SearchRoleMember searchRoleMember, Pageable pageable);
}
