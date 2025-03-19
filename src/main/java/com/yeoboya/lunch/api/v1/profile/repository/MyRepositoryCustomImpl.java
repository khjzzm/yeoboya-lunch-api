package com.yeoboya.lunch.api.v1.profile.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.api.v1.file.domain.QMemberProfileFile;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.domain.QAccount;
import com.yeoboya.lunch.api.v1.member.domain.QMember;
import com.yeoboya.lunch.api.v1.member.domain.QMemberInfo;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MyRepositoryCustomImpl implements MyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findMyInfo(String myId) {
        QMember member = QMember.member;
        QMemberInfo memberInfo = QMemberInfo.memberInfo;
        QAccount account = QAccount.account;
        QMemberProfileFile memberProfileFile = QMemberProfileFile.memberProfileFile;

        return queryFactory
                .selectFrom(member)
                .leftJoin(member.memberInfo, memberInfo).fetchJoin()
                .leftJoin(member.account, account).fetchJoin()
                .leftJoin(member.memberProfileFiles, memberProfileFile).fetchJoin()
                .where(member.loginId.eq(myId))
                .orderBy(
                        memberProfileFile.isDefault.desc()
                )
                .fetchOne();
    }
}
