package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class FreeBoardCreate {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;             //제목

    @NotBlank(message = "내용은 필수입니다.")
    private String content;           //콘텐츠

    private String category;

    private List<String> hashTag;     //해시태그

    @Digits(integer = 4, fraction = 0)
    private int pin;                  //비밀번호

    private boolean secret;           //비밀글여부

}
