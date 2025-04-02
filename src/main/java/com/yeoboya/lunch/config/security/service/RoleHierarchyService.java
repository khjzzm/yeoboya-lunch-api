package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.config.security.domain.RoleHierarchy;
import com.yeoboya.lunch.config.security.repository.RoleHierarchyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 역할 계층(Role Hierarchy) 관리를 담당하는 서비스 클래스.
 * 역할 계층 정보를 데이터베이스에서 조회하고, 계층 구조 문자열로 변환하여 반환한다.
 */
@Service
@RequiredArgsConstructor
public class RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    /**
     * 역할 계층 정보를 조회하여 계층 구조 문자열로 반환한다.
     *
     * @return 역할 계층을 나타내는 문자열
     * ROLE_ADMIN > ROLE_MANAGER
     * ROLE_MANAGER > ROLE_USER
     * ROLE_USER > ROLE_GUEST
     * ROLE_GUEST > ROLE_BLOCK
     */
    @Transactional
    public String findAllHierarchy() {
        return roleHierarchyRepository.findAllWithParentName().stream()
                .filter(roleHierarchy -> roleHierarchy.getParentName() != null) // 부모 역할이 존재하는 경우만 필터링
                .map(this::convertToHierarchyString) // 역할 계층을 문자열로 변환
                .collect(Collectors.joining(System.lineSeparator())); // 줄바꿈 문자로 연결하여 반환
    }

    /**
     * 역할 계층 정보를 "부모 역할 > 자식 역할" 형식의 문자열로 변환한다.
     *
     * @param roleHierarchy 변환할 역할 계층 객체
     * @return 변환된 역할 계층 문자열
     */
    private String convertToHierarchyString(RoleHierarchy roleHierarchy) {
        return roleHierarchy.getParentName().getChildName() // 부모 역할 이름 가져오기
                + " > "
                + roleHierarchy.getChildName(); // 자식 역할 이름 가져오기
    }
}
