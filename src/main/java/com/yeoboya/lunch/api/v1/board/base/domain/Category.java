package com.yeoboya.lunch.api.v1.board.base.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(length = 100)
    private String description;
}