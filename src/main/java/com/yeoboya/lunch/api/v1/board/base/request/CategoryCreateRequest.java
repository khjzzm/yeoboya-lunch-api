package com.yeoboya.lunch.api.v1.board.base.request;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryCreateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BoardType boardType;
}