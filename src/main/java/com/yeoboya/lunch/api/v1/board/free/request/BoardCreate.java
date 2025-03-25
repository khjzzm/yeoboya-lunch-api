package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * The {@code BoardCreate} class represents a board creation object.
 * It contains fields to store information about the board, such as email, title, hash tags, content, pin, and secret.
 * The class provides getter and setter methods for each field.
 *
 * @see FileBoardCreate
 */
@Setter
@Getter
@ToString
@Builder
public class BoardCreate {

    @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
    private String loginId;

    private String title;             //제목

    private List<String> hashTag;     //해시태그

    private String content;           //콘텐츠

    @Digits(integer = 4, fraction = 0)
    private int pin;                  //비밀번호

    private boolean secret;           //비밀글여부

}
