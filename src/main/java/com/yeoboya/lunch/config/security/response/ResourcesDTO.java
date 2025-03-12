package com.yeoboya.lunch.config.security.response;

import com.yeoboya.lunch.config.security.domain.Resources;
import com.yeoboya.lunch.config.security.domain.Role;
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

    public ResourcesDTO(Resources resources) {
        this.id = resources.getId();
        this.resourceName = resources.getResourceName();
        this.httpMethod = resources.getHttpMethod();
        this.orderNum = resources.getOrderNum();
        this.resourceType = resources.getResourceType();

        // ✅ RoleResources를 통해 Role 정보 추출
        this.roleDesc = resources.getRoleResources().stream()
                .map(roleResource -> roleResource.getRole().getRoleDesc()) // RoleResources → Role 변환
                .collect(Collectors.joining(", ")); // 쉼표로 구분하여 문자열로 저장
    }
}
