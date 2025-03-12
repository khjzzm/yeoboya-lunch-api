package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.response.MemberRoleResponse;
import com.yeoboya.lunch.config.annotation.AuthReload;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.UserSecurityStatus;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.repository.UserSecurityStatusRepository;
import com.yeoboya.lunch.config.security.reqeust.AuthorityRequest;
import com.yeoboya.lunch.config.security.reqeust.SecurityRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleService {

    private final MemberRepository memberRepository;
    private final UserSecurityStatusRepository userSecurityStatusRepository;
    private final RoleRepository roleRepository;
    private final Response response;

    @AuthReload
    @Transactional
    public ResponseEntity<Response.Body> updateAuthority(AuthorityRequest roleRequest) {
        try {
            Member targetMember = memberRepository.findByLoginId(roleRequest.getLoginId())
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found - " + roleRequest.getLoginId()));

            Role newRole = roleRepository.findByRole(roleRequest.getRole());

            targetMember.setRole(newRole);
            memberRepository.save(targetMember);
            return response.success(Code.UPDATE_SUCCESS);
        } catch (Exception e) {
            return response.fail(ErrorCode.FAIL, e.getMessage());
        }
    }

    @AuthReload
    public ResponseEntity<Response.Body> updateSecurityStatus(SecurityRequest securityRequest) {

        Member member = memberRepository.findByLoginId(securityRequest.getLoginId())
                .orElseThrow(() -> new EntityNotFoundException("Member with loginId " + securityRequest.getLoginId() + " is not found"));

        UserSecurityStatus userSecuritystatus = member.getUserSecurityStatus();
        userSecuritystatus.setEnabled(securityRequest.isEnabled());
        userSecuritystatus.setAccountNonLocked(securityRequest.isAccountNonLocked());
        userSecurityStatusRepository.save(userSecuritystatus);

        return null;
    }


    public ResponseEntity<Response.Body> getAuthorityList(Pageable pageable) {
        Page<MemberRoleResponse> withRolesInPages = memberRepository.findWithRolesInPages(pageable);

        Pagination pagination = new Pagination(
                withRolesInPages.getNumber() + 1,
                withRolesInPages.isFirst(),
                withRolesInPages.isLast(),
                withRolesInPages.isEmpty(),
                withRolesInPages.getTotalPages(),
                withRolesInPages.getTotalElements());

        Map<String, Object> data = Map.of(
                "list", withRolesInPages.getContent(),
                "pagination", pagination);

        return response.success(Code.SEARCH_SUCCESS, data);
    }

    @Transactional
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void createRole(Role role) {
        roleRepository.save(role);
    }


}
