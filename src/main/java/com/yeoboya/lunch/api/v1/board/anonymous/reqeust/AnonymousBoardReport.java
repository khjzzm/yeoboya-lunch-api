package com.yeoboya.lunch.api.v1.board.anonymous.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnonymousBoardReport {

    @Schema(description = "게시글 ID", example = "1")
    private Long boardId;

    @Schema(description = "신고 사유", example = "욕설 포함")
    private String reason;
}