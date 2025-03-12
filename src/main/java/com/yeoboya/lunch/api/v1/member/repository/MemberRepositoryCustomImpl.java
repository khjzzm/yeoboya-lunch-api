package com.yeoboya.lunch.api.v1.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.file.response.FileUploadResponse;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.api.v1.member.response.MemberRoleResponse;
import com.yeoboya.lunch.api.v1.member.response.QMemberResponse;
import com.yeoboya.lunch.api.v1.member.response.QMemberRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.yeoboya.lunch.api.v1.file.domain.QMemberProfileFile.memberProfileFile;
import static com.yeoboya.lunch.api.v1.member.domain.QAccount.account;
import static com.yeoboya.lunch.api.v1.member.domain.QMember.member;
import static com.yeoboya.lunch.api.v1.member.domain.QMemberInfo.memberInfo;
import static com.yeoboya.lunch.config.security.domain.QUserSecurityStatus.userSecurityStatus;
import static com.yeoboya.lunch.config.security.domain.QRole.role1;


@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Slice<MemberResponse> findMembersInPages(Pageable pageable) {
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
                .limit(pageable.getPageSize() + 1)  //페이지 사이즈
                .offset(pageable.getOffset())   //페이지번호
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
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
    public Page<MemberRoleResponse> findWithRolesInPages(Pageable pageable) {
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
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(member.countDistinct())
                .from(member)
                .leftJoin(member.userSecurityStatus, userSecurityStatus);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression likeMemberEmail(String email) {
        if (StringUtils.hasText(email)) {
            return member.email.like("%" + email + "%");
        }
        return null;
    }
}
