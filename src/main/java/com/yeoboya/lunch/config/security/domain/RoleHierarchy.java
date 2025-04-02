package com.yeoboya.lunch.config.security.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 역할 간 계층 구조(RBAC - Role-Based Access Control)
 */
@Entity
@Table(name="ROLE_HIERARCHY")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parentName", "roleHierarchy"})
@Builder
public class RoleHierarchy implements Serializable {

    @Id
    @Column(name = "ROLE_HIERARCHY_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_name")
    private String childName;

    //양방향 자기참조 관계
    //    ROLE_ADMIN
    //      └── ROLE_MANAGER
    //          └── ROLE_USER
    //              └── ROLE_GUEST
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_name", referencedColumnName = "child_name")
    private RoleHierarchy parentName;

    @Builder.Default
    @OneToMany(mappedBy = "parentName", cascade={CascadeType.ALL})
    private Set<RoleHierarchy> roleHierarchy = new HashSet<>();
}
