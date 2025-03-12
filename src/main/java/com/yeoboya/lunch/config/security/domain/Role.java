package com.yeoboya.lunch.config.security.domain;

import com.yeoboya.lunch.config.security.constants.Authority;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@ToString(of = {"role"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLES_ID", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private Authority role;

    @Column
    private String roleDesc;

    // ✅ ManyToMany 제거하고 OneToMany 설정
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoleResources> roleResources = new LinkedHashSet<>();
}