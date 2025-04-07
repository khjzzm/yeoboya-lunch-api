package com.yeoboya.lunch.api.v1.support.request;

import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class NoticeCreate {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull
    private Long categoryId;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 100, message = "작성자는 100자 이내로 입력해주세요.")
    private String author;

    @NotNull(message = "상단 고정 여부는 필수입니다.")
    private Boolean pinned;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 300, message = "첨부파일 URL은 300자 이내로 입력해주세요.")
    private String attachmentUrl;

    @NotNull(message = "공지 상태는 필수입니다.")
    private NoticeStatus status;
}