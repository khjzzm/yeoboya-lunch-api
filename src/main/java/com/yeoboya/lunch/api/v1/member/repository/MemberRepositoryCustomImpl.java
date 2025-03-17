package com.yeoboya.lunch.api.v1.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.reqeust.SearchMember;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.api.v1.member.response.MemberRoleResponse;
import com.yeoboya.lunch.api.v1.member.response.QMemberResponse;
import com.yeoboya.lunch.api.v1.member.response.QMemberRoleResponse;
import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.reqeust.SearchRoleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.yeoboya.lunch.api.v1.file.domain.QMemberProfileFile.memberProfileFile;
import static com.yeoboya.lunch.api.v1.member.domain.QAccount.account;
import static com.yeoboya.lunch.api.v1.member.domain.QMember.member;
import static com.yeoboya.lunch.api.v1.member.domain.QMemberInfo.memberInfo;
import static com.yeoboya.lunch.config.security.constants.Authority.fromKoreanName;
import static com.yeoboya.lunch.config.security.domain.QUserSecurityStatus.userSecurityStatus;
import static com.yeoboya.lunch.config.security.domain.QRole.role1;


@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<MemberResponse> findMembersInPages(SearchMember searchMember, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 동적 검색 조건 추가
        searchWithSpecifications(searchMember, predicate);

        List<MemberResponse> content = query.select(
                        new QMemberResponse(
                                member.loginId, member.email, member.provider,
                                member.name, account.bankName, account.accountNumber,
                                memberInfo.bio, memberInfo.nickName, memberInfo.phoneNumber
                        )
                )
                .from(member)
                .leftJoin(member.account, account)
                .leftJoin(member.memberInfo, memberInfo)
                .where(predicate)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(member.countDistinct())
                .from(member)
                .where(predicate)
                .leftJoin(member.account, account)
                .leftJoin(member.memberInfo, memberInfo);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    @Override
    public MemberInfo getMemberInfo(String loginId) {
        return query.selectFrom(memberInfo)
                .leftJoin(memberInfo.member, member)
                .where(memberInfo.member.loginId.eq(loginId))
                .fetchOne();
    }

    @Override
    public MemberResponse memberProfile(String loginId) {
        return query.select(
                        new QMemberResponse(
                                member.loginId, member.email, member.provider, member.name,
                                account.bankName, account.accountNumber,
                                memberInfo.bio, memberInfo.nickName, memberInfo.phoneNumber
                        )
                )
                .from(member)
                .leftJoin(member.account, account)
                .leftJoin(member.memberInfo, memberInfo)
                .where(member.loginId.eq(loginId))
                .fetchOne();
    }

    @Override
    public List<MemberProfileFile> profileImg(String loginId) {
        return query
                .select(memberProfileFile)
                .from(member)
                .leftJoin(member.memberProfileFiles, memberProfileFile)
                .where(member.loginId.eq(loginId).and(memberProfileFile.isNotNull()))
                .fetch();
    }

    @Override
    public Page<MemberRoleResponse> findWithRolesInPages(SearchRoleMember searchRoleMember, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 동적 검색 조건 추가
        searchWithSpecifications(searchRoleMember, predicate);

        List<MemberRoleResponse> content = query.select(
                        new QMemberRoleResponse(
                                member.loginId, member.email, member.provider, member.name,
                                member.role.roleDesc, member.role.role,
                                userSecurityStatus.isEnabled, userSecurityStatus.isAccountNonLocked
                        )
                )
                .from(member)
                .leftJoin(member.role, role1)
                .leftJoin(member.userSecurityStatus, userSecurityStatus)
                .limit(pageable.getPageSize())  //페이지 사이즈
                .offset(pageable.getOffset())   //페이지번호
                .where(predicate)
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(member.countDistinct())
                .from(member)
                .leftJoin(member.role, role1)
                .leftJoin(member.userSecurityStatus, userSecurityStatus)
                .where(predicate);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    //회원검색
    private static void searchWithSpecifications(SearchMember searchMember, BooleanBuilder predicate) {
        if (StringUtils.hasText(searchMember.getLoginId())) {
            predicate.and(member.loginId.contains(searchMember.getLoginId()));
        }
        if (StringUtils.hasText(searchMember.getName())) {
            predicate.and(member.name.contains(searchMember.getName()));
        }
        if (StringUtils.hasText(searchMember.getNickName())) {
            predicate.and(memberInfo.nickName.contains(searchMember.getNickName()));
        }

        // SearchRoleMember 추가 조건
        if (searchMember instanceof SearchRoleMember) {
            SearchRoleMember searchRoleMember = (SearchRoleMember) searchMember;
            List<Authority> authorities = searchRoleMember.getAuthority();
            if (authorities != null && !authorities.isEmpty()) {
                predicate.and(member.role.role.in(authorities));
            }
        }
    }

}
