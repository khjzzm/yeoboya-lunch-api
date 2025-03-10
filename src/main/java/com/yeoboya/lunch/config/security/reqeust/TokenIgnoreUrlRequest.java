package com.yeoboya.lunch.config.security.reqeust;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenIgnoreUrlRequest {

    @JsonProperty("isIgnore")
    private boolean ignore;

    private String url;
}
