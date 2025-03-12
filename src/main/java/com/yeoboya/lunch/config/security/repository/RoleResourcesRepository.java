package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.Resource;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleResourcesRepository extends JpaRepository<RoleResource, Long> {
    Optional<RoleResource> findByResourceAndRole(Resource resource, Role role);

    Optional<RoleResource> findByResource(Resource resource);
}