package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class FreeBoardCreate {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title; // 제목

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 10000, message = "내용은 최대 10,000자까지 입력 가능합니다.")
    private String content; // 콘텐츠

    @NotNull
    private Long categoryId;

    @Size(max = 10, message = "해시태그는 최대 10개까지 입력 가능합니다.")
    private List<@Size(max = 30, message = "각 해시태그는 최대 30자까지 입력 가능합니다.") String> hashTag;

    @Digits(integer = 4, fraction = 0, message = "비밀번호는 최대 4자리 숫자여야 합니다.")
    private int pin; // 비밀번호

    private boolean secret; // 비밀글 여부

}
