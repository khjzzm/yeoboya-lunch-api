package com.yeoboya.lunch.config.security.reqeust;

import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResource;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleResourcesRequest {
    private Long resourceId;                // 리소스 번호
    private Authority role;

    public static RoleResource toDomain(Resource resource, Role role) {
        return new RoleResource(resource, role);
    }

}
