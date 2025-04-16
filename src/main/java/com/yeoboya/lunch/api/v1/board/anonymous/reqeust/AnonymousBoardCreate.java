package com.yeoboya.lunch.api.v1.board.anonymous.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class AnonymousBoardCreate {

    @Schema(description = "닉네임", example = "익명1")
    @NotBlank
    private String nickname;

    @Schema(description = "게시글 내용", example = "이직을 할지 말지 고민입니다.")
    @NotBlank
    private String content;

    @Schema(description = "비밀번호 (삭제/수정용)", example = "1234")
    @NotBlank
    private String password;

    @Schema(description = "삭제 예정 시간", example = "2025-05-01T12:00:00")
    private LocalDateTime deleteAt; // nullable
}