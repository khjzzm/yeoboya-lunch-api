package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.domain.Resources;
import com.yeoboya.lunch.config.security.domain.TokenIgnoreUrl;
import com.yeoboya.lunch.config.security.repository.ResourcesRepository;
import com.yeoboya.lunch.config.security.repository.RoleRepository;
import com.yeoboya.lunch.config.security.repository.TokenIgnoreUrlRepository;
import com.yeoboya.lunch.config.security.reqeust.ResourcesRequest;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import com.yeoboya.lunch.config.security.response.ResourcesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ResourcesService {

    private final ResourcesRepository resourcesRepository;
    private final RoleRepository roleRepository;
    private final TokenIgnoreUrlRepository tokenIgnoreUrlRepository;
    private final Response response;


    //리소스 조회
    public Resources selectResources(long id) {
        return resourcesRepository.findById(id).orElse(new Resources());
    }

    //리소스 전체 조회
    //todo return page
    public ResponseEntity<Response.Body> fetchAllResources(Pageable pageable) {
        Page<Resources> resourcesList = resourcesRepository.findAll(pageable);
        List<ResourcesDTO> resourcesDTOList = resourcesList.stream()
                .map(ResourcesDTO::new)
                .collect(Collectors.toList());
        return response.success(Code.SEARCH_SUCCESS, resourcesDTOList);
    }

    //리소스
    public ResponseEntity<Response.Body> addResources(ResourcesRequest resourcesRequest) {
        Optional<Resources> byResourceName = resourcesRepository.findByResourceName(resourcesRequest.getResourceName());

        if (byResourceName.isPresent()) {
            // resourceName가 존재하면 기존 데이터를 업데이트한다.
            Resources existingResource = byResourceName.get();

            // resourceName이 같은 Resources 필드들을 resourcesRequest 값으로 업데이트
            existingResource.setHttpMethod(resourcesRequest.getHttpMethod());
            existingResource.setResourceType(resourcesRequest.getResourceType());
            Optional.ofNullable(resourcesRequest.getRole())
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .ifPresent(roleOptional -> {
                        existingResource.getRoleSet().clear(); // 기존 역할 세트를 먼저 지웁니다.
                        existingResource.getRoleSet().add(roleOptional.get()); // 새로 받은 역할을 추가합니다.
                    });
            resourcesRepository.save(existingResource);
        } else {
            // resourceName이 존재하지 않는다면, 새로운 데이터를 저장한다.
            Resources resources = Resources.createResources(resourcesRequest);

            int lastOrderNum = this.getLastOrderNum();
            resources.setOrderNum(lastOrderNum + 1);

            Optional.ofNullable(resourcesRequest.getRole())
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .ifPresent(roleOptional ->
                            resources.setRoleSet(Collections.singleton(roleOptional.get()))
                    );
            resourcesRepository.save(resources);
        }


        return response.success(Code.SAVE_SUCCESS);
    }

    //리소스
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }


    private int getLastOrderNum() {
        Resources lastResource = resourcesRepository.findTopByOrderByOrderNumDesc();
        return lastResource != null ? lastResource.getOrderNum() : 0;
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
