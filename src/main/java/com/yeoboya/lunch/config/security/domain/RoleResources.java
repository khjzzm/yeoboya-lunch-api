package com.yeoboya.lunch.config.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ROLE_RESOURCES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID", nullable = false)
    private Resources resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    public RoleResources(Resources resource, Role roleId) {
        this.resource = resource;
        this.role = roleId;
    }
}