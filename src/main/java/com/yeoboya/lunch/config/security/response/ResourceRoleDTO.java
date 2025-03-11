package com.yeoboya.lunch.config.security.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ResourceRoleDTO {
    private Long roleResourceId;   // RoleResources 테이블의 ID
    private String roleDesc;       // Role 설명
    private Long resourceId;       // Resource 테이블의 ID
    private String resourceName;   // 리소스 이름
    private String resourceDesc;   // 리소스 설명
    private Integer orderNum;      // 리소스 순서
    private String resourceType;   // 리소스 타입
    private String httpMethod;     // HTTP 메서드 (GET, POST 등)

    @QueryProjection
    public ResourceRoleDTO(Long roleResourceId, String roleDesc, Long resourceId, String resourceName, String resourceDesc, Integer orderNum, String resourceType, String httpMethod) {
        this.roleResourceId = roleResourceId;
        this.roleDesc = roleDesc;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceDesc = resourceDesc;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
        this.httpMethod = httpMethod;
    }
}