package com.yeoboya.lunch.api.v1.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String message;
}