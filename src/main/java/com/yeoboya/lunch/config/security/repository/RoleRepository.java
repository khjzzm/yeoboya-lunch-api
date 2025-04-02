package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.constants.Authority;
import com.yeoboya.lunch.config.security.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRole(Authority role);

    Optional<Role> findByRole(String role);

    boolean existsByRole(Authority role);

    @Override
    void delete(Role role);

}
