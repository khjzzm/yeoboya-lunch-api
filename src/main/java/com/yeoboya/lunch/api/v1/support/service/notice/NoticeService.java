package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.file.repository.NoticeFileRepository;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import com.yeoboya.lunch.api.v1.support.domain.NoticeReadStatus;
import com.yeoboya.lunch.api.v1.support.repository.NoticeReadStatusRepository;
import com.yeoboya.lunch.api.v1.support.repository.NoticeRepository;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeResponse;
import com.yeoboya.lunch.api.v1.support.response.NoticeSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeFileRepository noticeFileRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeReadStatusRepository noticeReadStatusRepository;
    private final MemberRepository memberRepository;

    private final Response response;

    @Transactional
    public Notice createNotice(NoticeRequest noticeRequest) {
        Notice notice = Notice.builder()
                .title(noticeRequest.getTitle())
                .content(noticeRequest.getContent())
                .category(noticeRequest.getCategory())
                .author(noticeRequest.getAuthor())
                .priority(noticeRequest.getPriority().ordinal())
                .startDate(noticeRequest.getStartDate())
                .endDate(noticeRequest.getEndDate())
                .attachmentUrl(noticeRequest.getAttachmentUrl())
                .status(noticeRequest.getStatus())
                .build();

        List<String> imageUrlsInContent = this.extractImageUrls(noticeRequest.getContent());
        List<NoticeFile> files = noticeFileRepository.findByImageUrlIn(imageUrlsInContent);
        for (NoticeFile file : files) {
            notice.addNoticeFile(file); // 연관관계 설정
        }

        return noticeRepository.save(notice);
    }

    @Transactional
    public void markNoticeAsRead(Long noticeId, String loginId) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if (optionalMember.isEmpty()) return; // 비회원이면 아무 작업 안 함

        Member member = optionalMember.get();
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        noticeReadStatusRepository.findByMemberAndNotice(member, notice)
                .filter(status -> status.getReadAt() == null)
                .ifPresentOrElse(readStatus -> {
                    // 기존 기록 있음 → 안 읽었을 경우 업데이트
                    readStatus.setReadAt(LocalDateTime.now());
                    notice.setViewCount(notice.getViewCount() + 1);
                    noticeReadStatusRepository.save(readStatus);
                    noticeRepository.save(notice);
                }, () -> {
                    // 기록 없음 → 새로 생성
                    NoticeReadStatus newStatus = NoticeReadStatus.builder()
                            .member(member)
                            .notice(notice)
                            .readAt(LocalDateTime.now())
                            .build();
                    notice.setViewCount(notice.getViewCount() + 1);
                    noticeReadStatusRepository.save(newStatus);
                    noticeRepository.save(notice);
                });
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllNoticesWithReadStatus(String loginId, NoticeSearchCondition condition, Pageable pageable) {
        Page<NoticeSummaryResponse> notices = noticeRepository.searchNotices(condition, pageable);

        List<NoticeSummaryResponse> content = notices.getContent();

        Pagination pagination = new Pagination(
                notices.getNumber() + 1,
                notices.isFirst(),
                notices.isLast(),
                notices.isEmpty(),
                notices.getTotalPages(),
                notices.getTotalElements());

        return Map.of(
                "list", content,
                "pagination", pagination);
    }

    @Transactional(readOnly = true)
    public NoticeResponse getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        return NoticeResponse.from(notice); // isRead는 상세 조회에서 필요 시 확장
    }

    @Transactional
    public Notice updateNotice(Long noticeId, NoticeRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setCategory(request.getCategory());
        notice.setAuthor(request.getAuthor());
        notice.setPriority(request.getPriority().ordinal());
        notice.setStartDate(request.getStartDate());
        notice.setEndDate(request.getEndDate());
        notice.setAttachmentUrl(request.getAttachmentUrl());
        notice.setStatus(request.getStatus());

        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));

        // 연관된 파일도 삭제 (연관관계가 설정되어 있어 orphanRemoval = true 라면 자동 삭제됨)
        noticeRepository.delete(notice);
    }

    private List<String> extractImageUrls(String htmlContent) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"'>]+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}