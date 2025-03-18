package com.yeoboya.lunch.api.v1.member.service;

import com.yeoboya.lunch.api.v1.common.response.*;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.file.repository.MemberProfileFileRepository;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.repository.AccountRepository;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import com.yeoboya.lunch.api.v1.member.response.MemberProjections.MemberAccount;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.config.annotation.EnsureMemberExists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final Response response;

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final MemberProfileFileRepository memberProfileFileRepository;

    public MemberService(Response response, MemberRepository memberRepository, AccountRepository accountRepository, FileServiceS3 fileServiceS3, MemberProfileFileRepository memberProfileFileRepository) {
        this.response = response;
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.memberProfileFileRepository = memberProfileFileRepository;
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
    public MemberResponse memberProfile(String loginId) {
        MemberResponse memberResponse = memberRepository.memberProfile(loginId);
        List<MemberProfileFile> memberProfileFiles = memberRepository.profileImg(loginId);

//        List<ProfileResponse> responses = memberProfileFiles.stream()
//                .map(ProfileResponse::apply)
//                .collect(Collectors.toList());

        memberResponse.setProfileImg(null);

        if (StringUtils.hasText(memberResponse.getAccountNumber())) {
            memberResponse.setAccount(true);
        }
        return memberResponse;
    }

}

