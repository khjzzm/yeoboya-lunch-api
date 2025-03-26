package com.yeoboya.lunch.api.v1.member.service;

import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.*;
import com.yeoboya.lunch.api.v1.file.repository.MemberProfileFileRepository;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.AccountRepository;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import com.yeoboya.lunch.api.v1.member.response.MemberProjections.MemberAccount;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.config.annotation.EnsureMemberExists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(Response response, MemberRepository memberRepository, AccountRepository accountRepository, FileServiceS3 fileServiceS3, MemberProfileFileRepository memberProfileFileRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Map<String, Object> memberList(SearchMember searchMember, Pageable pageable) {
        Page<MemberResponse> membersInPages = memberRepository.findMembersInPages(searchMember, pageable);

        Pagination pagination = new Pagination(
                membersInPages.getNumber() + 1,
                membersInPages.isFirst(),
                membersInPages.isLast(),
                membersInPages.isEmpty(),
                membersInPages.getTotalPages(),
                membersInPages.getTotalElements());

        return Map.of(
                "list", membersInPages.getContent(),
                "pagination", pagination);
    }


    public MemberAccount memberAccount(String loginId) {
        return memberRepository.findByLoginId(loginId, MemberAccount.class);
    }

    @Transactional
    @EnsureMemberExists
    public MemberResponse getMemberProfile(String loginId) {
        String hasId = StringUtils.hasText(loginId) ? loginId : SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.getMemberProfile(hasId);
    }


    public Optional<Member> getOptionalMember(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    public Member getMember(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found - " + loginId));
    }


}

