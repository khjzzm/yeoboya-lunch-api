package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class BoardEdit {

    private String title;             //제목
    private List<String> hashTag;     //해시태그
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 10000, message = "내용은 최대 10,000자까지 입력 가능합니다.")
    private String content;           //콘텐츠
    private String category;

    @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 숫자 4자리여야 합니다.")
    private String pin;
    private boolean secret;           //비밀글여부
    private Long categoryId;

}
