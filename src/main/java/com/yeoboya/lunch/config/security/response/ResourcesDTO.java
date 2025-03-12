package com.yeoboya.lunch.config.security.response;

import com.yeoboya.lunch.config.security.domain.Resource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ResourcesDTO {
    private Long id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleDesc;

    public ResourcesDTO(Resource resource) {
        this.id = resource.getId();
        this.resourceName = resource.getResourceName();
        this.httpMethod = resource.getHttpMethod();
        this.orderNum = resource.getOrderNum();
        this.resourceType = resource.getResourceType();

        // ✅ RoleResources를 통해 Role 정보 추출
        this.roleDesc = resource.getRoleResources().stream()
                .map(roleResource -> roleResource.getRole().getRoleDesc()) // RoleResources → Role 변환
                .collect(Collectors.joining(", ")); // 쉼표로 구분하여 문자열로 저장
    }
}
