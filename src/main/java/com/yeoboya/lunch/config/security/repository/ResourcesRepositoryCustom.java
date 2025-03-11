package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.config.security.response.ResourceRoleDTO;

import java.util.List;

public interface ResourcesRepositoryCustom {

    List<ResourceRoleDTO> findRoleResources();

}
