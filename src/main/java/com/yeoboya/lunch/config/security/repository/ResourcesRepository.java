package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.domain.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourcesRepository extends JpaRepository<Resources, Long>, ResourcesRepositoryCustom {

    Resources findTopByOrderByOrderNumDesc();

    Optional<Resources> findByResourceName(String resourceName);

    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("SELECT r FROM Resources r LEFT JOIN FETCH r.roleResources rr LEFT JOIN FETCH rr.role")
    List<Resources> findAllResourcesWithRoles();
}
