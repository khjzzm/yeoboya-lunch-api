package com.yeoboya.lunch.config.security.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboya.lunch.config.security.response.QResourceRoleDTO;
import com.yeoboya.lunch.config.security.response.ResourceRoleDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboya.lunch.config.security.domain.QResources.resources;
import static com.yeoboya.lunch.config.security.domain.QRole.role1;
import static com.yeoboya.lunch.config.security.domain.QRoleResources.roleResources;

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
                        roleResources.id,
                        role1.roleDesc,
                        resources.id,
                        resources.resourceName,
                        resources.resourceDesc,
                        resources.orderNum,
                        resources.resourceType,
                        resources.httpMethod
                ))
                .from(resources)
                .leftJoin(roleResources)
                .on(roleResources.resource.eq(resources))
                .leftJoin(roleResources.role, role1)
                .fetch();
    }
}
