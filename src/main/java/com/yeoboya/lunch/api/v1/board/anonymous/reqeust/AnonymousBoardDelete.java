package com.yeoboya.lunch.api.v1.board.anonymous.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AnonymousBoardDelete {

    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;
}