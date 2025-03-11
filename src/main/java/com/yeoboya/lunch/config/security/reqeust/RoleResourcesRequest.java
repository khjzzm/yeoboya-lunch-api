package com.yeoboya.lunch.config.security.reqeust;

import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.Resources;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResources;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleResourcesRequest {
    private Long resourceId;                // 리소스 번호
    private Authority role;

    public static RoleResources toDomain(Resources resources, Role role) {
        return new RoleResources(resources, role);
    }

}
