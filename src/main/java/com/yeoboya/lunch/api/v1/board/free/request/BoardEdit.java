package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class BoardEdit {

    private String title;             //제목
    private List<String> hashTag;     //해시태그
    private String content;           //콘텐츠
    private String category;
    @Digits(integer = 4, fraction = 0)
    private int pin;                  //비밀번호
    private boolean secret;           //비밀글여부
    private Long categoryId;

}
