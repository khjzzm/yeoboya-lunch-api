package com.yeoboya.lunch.api.v1.board.anonymous.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class AnonymousBoardUpdate {

    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;

    @Schema(description = "수정할 내용", example = "수정된 내용입니다.")
    @NotBlank
    private String content;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;

    @Schema(description = "삭제 예정 시간", example = "2025-05-01T12:00:00")
    private OffsetDateTime deleteAt; // nullable
}