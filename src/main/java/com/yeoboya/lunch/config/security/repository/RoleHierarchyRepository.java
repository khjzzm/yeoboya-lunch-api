package com.yeoboya.lunch.config.security.repository;


import com.yeoboya.lunch.config.security.domain.RoleHierarchy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

    @EntityGraph(attributePaths = "parentName")
    @Query("SELECT r FROM RoleHierarchy r")
    List<RoleHierarchy> findAllWithParentName();

    RoleHierarchy findByChildName(String roleName);
}
