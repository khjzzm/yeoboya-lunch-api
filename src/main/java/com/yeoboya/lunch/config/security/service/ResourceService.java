package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.annotation.AuthReload;
import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResource;
import com.yeoboya.lunch.config.security.domain.TokenIgnoreUrl;
import com.yeoboya.lunch.config.security.response.ResourceRoleDTO;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.repository.RoleResourcesRepository;
import com.yeoboya.lunch.config.security.repository.TokenIgnoreUrlRepository;
import com.yeoboya.lunch.config.security.reqeust.RoleResourcesRequest;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final RoleRepository roleRepository;
    private final ResourcesRepository resourcesRepository;
    private final RoleResourcesRepository roleResourcesRepository;
    private final TokenIgnoreUrlRepository tokenIgnoreUrlRepository;

    private final Response response;


    //리소스 전체 조회
    public ResponseEntity<Response.Body> fetchAllResources(Pageable pageable) {
        List<ResourceRoleDTO> roleResourcesByRoleId = resourcesRepository.findRoleResources();
        return response.success(Code.SEARCH_SUCCESS, roleResourcesByRoleId);
    }

    @AuthReload
    public ResponseEntity<Response.Body> updateRoleResources(RoleResourcesRequest roleResourcesRequest) {
        // 리소스 조회
        Resource resource = resourcesRepository.findById(roleResourcesRequest.getResourceId())
                .orElseThrow(() -> new EntityNotFoundException("리소스를 찾을 수 없습니다: " + roleResourcesRequest.getResourceId()));

        // 역할 조회
        Role role = roleRepository.findByRole(roleResourcesRequest.getRole());

        // 기존 데이터 확인 (같은 리소스 + 역할이 존재하는지 체크)
        Optional<RoleResource> existingRoleResource = roleResourcesRepository.findByResource(resource);

        if (existingRoleResource.isPresent()) {
            RoleResource roleResource = existingRoleResource.get();
            roleResource.setResource(resource);
            roleResource.setRole(role);
            roleResourcesRepository.save(roleResource);
            return response.success(Code.UPDATE_SUCCESS);
        }

        RoleResource newRoleResource = RoleResourcesRequest.toDomain(resource, role);
        roleResourcesRepository.save(newRoleResource);
        return response.success(Code.SAVE_SUCCESS);
    }


    //리소스 삭제
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }

    public ResponseEntity<Response.Body> findTokenIgnoreUrl() {
        List<TokenIgnoreUrl> tokenIgnoreUrlsByUrl = tokenIgnoreUrlRepository.getTokenIgnoreUrls();
        return response.success(Code.SEARCH_SUCCESS, tokenIgnoreUrlsByUrl);
    }

    public ResponseEntity<Response.Body> saveTokenIgnoreUrl(TokenIgnoreUrlRequest tokenIgnoreUrlRequest) {
        int i = tokenIgnoreUrlRepository.insertOrUpdateTokenIgnoreUrl(tokenIgnoreUrlRequest);
        return response.success(i);
    }

    public ResponseEntity<Response.Body> deleteTokenIgnoreUrl(Long id) {
        int i = tokenIgnoreUrlRepository.deleteTokenIgnoreUrl(id);
        return response.success(i);
    }


}
