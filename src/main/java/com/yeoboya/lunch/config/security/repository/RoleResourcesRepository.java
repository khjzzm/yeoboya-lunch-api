package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.Resources;
import com.yeoboya.lunch.config.security.domain.Role;
import com.yeoboya.lunch.config.security.domain.RoleResources;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleResourcesRepository extends JpaRepository<RoleResources, Long> {
    Optional<RoleResources> findByResourceAndRole(Resources resource, Role role);

    Optional<RoleResources> findByResource(Resources resources);
}