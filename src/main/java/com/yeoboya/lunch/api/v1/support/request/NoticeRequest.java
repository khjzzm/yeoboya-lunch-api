package com.yeoboya.lunch.api.v1.support.request;

import com.yeoboya.lunch.api.v1.support.constant.NoticePriority;
import com.yeoboya.lunch.api.v1.support.constant.NoticeStatus;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class NoticeRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(max = 50, message = "카테고리는 50자 이내로 입력해주세요.")
    private String category;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 100, message = "작성자는 100자 이내로 입력해주세요.")
    private String author;

    @NotNull(message = "우선순위는 필수입니다.")
    private NoticePriority priority;

    @PastOrPresent(message = "시작일은 현재 시각 이전 또는 같아야 합니다.")
    private LocalDateTime startDate;

    @FutureOrPresent(message = "종료일은 현재 시각 또는 이후여야 합니다.")
    private LocalDateTime endDate;

    @Size(max = 300, message = "첨부파일 URL은 300자 이내로 입력해주세요.")
    private String attachmentUrl;

    @NotNull(message = "공지 상태는 필수입니다.")
    private NoticeStatus status;
}