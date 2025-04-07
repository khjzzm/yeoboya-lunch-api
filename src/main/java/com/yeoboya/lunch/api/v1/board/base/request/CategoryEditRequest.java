package com.yeoboya.lunch.api.v1.board.base.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryEditRequest {
    @NotBlank
    private String name;

    private String description;
}