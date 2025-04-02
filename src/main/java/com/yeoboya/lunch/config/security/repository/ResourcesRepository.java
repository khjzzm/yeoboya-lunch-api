package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.Resource;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourcesRepository extends JpaRepository<Resource, Long>, ResourcesRepositoryCustom {

    Resource findTopByOrderByOrderNumDesc();

    Optional<Resource> findByResourceName(String resourceName);

    Resource findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @EntityGraph(attributePaths = "roleResources")
    @Query("SELECT r FROM Resource r")
    List<Resource> findAllWithRoleResources();

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.roleResources rr LEFT JOIN FETCH rr.role")
    List<Resource> findAllResourcesWithRoles();
}
