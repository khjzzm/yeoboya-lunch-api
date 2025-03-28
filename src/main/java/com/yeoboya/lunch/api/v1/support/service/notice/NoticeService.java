package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.domain.NoticeFile;
import com.yeoboya.lunch.api.v1.file.repository.NoticeFileRepository;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.service.MemberService;
import com.yeoboya.lunch.api.v1.support.domain.Notice;
import com.yeoboya.lunch.api.v1.support.domain.NoticeReadStatus;
import com.yeoboya.lunch.api.v1.support.repository.NoticeReadStatusRepository;
import com.yeoboya.lunch.api.v1.support.repository.NoticeRepository;
import com.yeoboya.lunch.api.v1.support.request.NoticeRequest;
import com.yeoboya.lunch.api.v1.support.request.NoticeSearchCondition;
import com.yeoboya.lunch.api.v1.support.response.NoticeDetailResponse;
import com.yeoboya.lunch.api.v1.support.response.NoticeProjection;
import com.yeoboya.lunch.config.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeFileRepository noticeFileRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeReadStatusRepository noticeReadStatusRepository;

    private final MemberService memberService;
    private final NoticeReplyService replyService;
    private final NoticeLikeService likeService;

    private final Response response;

    @Transactional
    public Notice createNotice(NoticeRequest noticeRequest) {
        Notice notice = Notice.createNotice(noticeRequest);
        List<String> imageUrlsInContent = this.extractImageUrls(noticeRequest.getContent());
        List<NoticeFile> files = noticeFileRepository.findByImageUrlIn(imageUrlsInContent);
        for (NoticeFile file : files) {
            notice.addNoticeFile(file); // 연관관계 설정
        }

        return noticeRepository.save(notice);
    }

    @Transactional
    public void markNoticeAsRead(Long noticeId, String loginId) {
        Optional<Member> optionalMember = memberService.getOptionalMember(loginId);
        if (optionalMember.isEmpty()) return; // 비회원이면 아무 작업 안 함

        Member member = optionalMember.get();
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("Notice not found"));

        noticeReadStatusRepository.findByMemberAndNotice(member, notice).ifPresentOrElse(readStatus -> {
            notice.setViewCount(notice.getViewCount() + 1);
            noticeRepository.save(notice);
        }, () -> {
            NoticeReadStatus newStatus = NoticeReadStatus.builder().member(member).notice(notice).readAt(LocalDateTime.now()).build();
            notice.setViewCount(notice.getViewCount() + 1);
            noticeReadStatusRepository.save(newStatus);
            noticeRepository.save(notice);
        });
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllNoticesWithReadStatus(NoticeSearchCondition condition, Pageable pageable) {
        Page<NoticeProjection> notices = noticeRepository.searchNotices(condition, pageable);
        List<NoticeProjection> content = notices.getContent();

        Pagination pagination = new Pagination(notices.getNumber() + 1, notices.isFirst(), notices.isLast(), notices.isEmpty(), notices.getTotalPages(), notices.getTotalElements());

        return Map.of("list", content, "pagination", pagination);
    }

    @Transactional
    public NoticeDetailResponse getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        Optional<String> loginIdOpt = SecurityUtils.getCurrentUserLoginId();

//        트랜잭션 중첩 호출 수정해야함 (내부적으로 @Transactional 수정용 메서드를 호출 오류)
        loginIdOpt.ifPresent(loginId -> this.markNoticeAsRead(noticeId, loginId));

        boolean hasLiked = loginIdOpt
                .map(loginId -> likeService.hasLiked(loginId, noticeId))
                .orElse(false);

        return NoticeDetailResponse.from(notice, hasLiked); // isRead는 상세 조회에서 필요 시 확장
    }

    @Transactional
    public Notice updateNotice(Long noticeId, NoticeRequest request) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setCategory(request.getCategory());
        notice.setAuthor(request.getAuthor());
        notice.setPinned(request.getPinned());
        notice.setStartDate(request.getStartDate());
        notice.setEndDate(request.getEndDate());
        notice.setAttachmentUrl(request.getAttachmentUrl());
        notice.setStatus(request.getStatus());

        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));

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

    public ResponseEntity<Response.Body> createReply(@Valid ReplyCreateRequest replyCreateRequest) {
        return replyService.createReply(replyCreateRequest);
    }

    public ResponseEntity<Response.Body> fetchBoardReplies(BoardSearchCondition search, Pageable pageable) {
        return replyService.fetchBoardReplies(search, pageable);
    }

    public ResponseEntity<Response.Body> deleteReply(Long replyId) {
        return replyService.deleteReply(replyId);
    }

    public ResponseEntity<Response.Body> likePost(Long noticeId) {
        return likeService.likePost(noticeId);
    }

    public ResponseEntity<Response.Body> unlikePost(Long noticeId) {
        return likeService.unlikePost(noticeId);
    }


}