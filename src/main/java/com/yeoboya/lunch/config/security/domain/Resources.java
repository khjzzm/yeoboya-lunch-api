package com.yeoboya.lunch.config.security.domain;

import com.yeoboya.lunch.config.security.domain.RoleResources;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESOURCE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"resource_name", "http_method"}) // 복합 유니크 설정
        })
@Data
@ToString(exclude = {"roleResources"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resources implements Serializable {

    @Id
    @Column(name = "RESOURCES_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_desc")
    private String resourceDesc;

    // ✅ ManyToMany 제거하고 OneToMany 설정
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoleResources> roleResources = new HashSet<>();

}