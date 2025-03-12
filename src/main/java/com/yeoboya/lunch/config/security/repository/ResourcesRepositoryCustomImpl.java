package com.yeoboya.lunch.config.security.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.config.security.response.QResourceRoleDTO;
import com.yeoboya.lunch.config.security.response.ResourceRoleDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.config.security.domain.QResource.resource;
import static com.yeoboya.lunch.config.security.domain.QRole.role1;
import static com.yeoboya.lunch.config.security.domain.QRoleResource.roleResource;

@Repository
public class ResourcesRepositoryCustomImpl implements ResourcesRepositoryCustom {

    private final JPAQueryFactory query;

    public ResourcesRepositoryCustomImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<ResourceRoleDTO> findRoleResources() {
        return query
                .select(new QResourceRoleDTO(
                        roleResource.id,
                        role1.roleDesc,
                        resource.id,
                        resource.resourceName,
                        resource.resourceDesc,
                        resource.orderNum,
                        resource.resourceType,
                        resource.httpMethod
                ))
                .from(resource)
                .leftJoin(roleResource)
                .on(roleResource.resource.eq(resource))
                .leftJoin(roleResource.role, role1)
                .fetch();
    }
}
