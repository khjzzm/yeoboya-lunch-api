package com.yeoboya.lunch.api.v1.support.service.notice;

import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.NoticeFileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeFile;
import com.yeoboya.lunch.api.v1.support.repository.notice.NoticeFileRepository;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.service.MemberService;
import com.yeoboya.lunch.api.v1.support.domain.notice.Notice;
import com.yeoboya.lunch.api.v1.support.domain.notice.NoticeReadStatus;
import com.yeoboya.lunch.api.v1.support.repository.notice.NoticeReadStatusRepository;
import com.yeoboya.lunch.api.v1.support.repository.notice.NoticeRepository;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    //repository
    private final NoticeFileRepository noticeFileRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeReadStatusRepository noticeReadStatusRepository;

    //service
    private final MemberService memberService;
    private final NoticeLikeService likeService;
    private final NoticeReplyService replyService;
    private final NoticeFileAttachService fileAttachService;
    private final FileServiceS3 fileServiceS3;

    @Transactional
    public NoticeDetailResponse createNotice(NoticeRequest noticeRequest) {
        // 1. Notice 엔티티 생성
        Notice notice = Notice.createNotice(noticeRequest);

        // 2. 먼저 Notice 저장 (boardId 확보를 위해)
        Notice savedNotice = noticeRepository.save(notice);

        // 3. 본문에서 파일 추출 후 Notice와 매핑
        fileAttachService.attachFilesFromContent(noticeRequest.getContent(), savedNotice);
        return NoticeDetailResponse.from(savedNotice);
    }

    @Transactional
    public FileResponse uploadImage(MultipartFile file) {
        Function<FileResponse, NoticeFileResponse> responseMapper = NoticeFileResponse::apply;
        FileResponse fileResponse = fileServiceS3.upload(file, Directory.NOTICE, responseMapper);
        NoticeFile noticeFile = NoticeFile.from(fileResponse);
        noticeFileRepository.save(noticeFile);
        return fileResponse;
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

        Pagination pagination = new Pagination(
                notices.getNumber() + 1,
                notices.isFirst(),
                notices.isLast(),
                notices.isEmpty(),
                notices.getTotalPages(),
                notices.getTotalElements()
        );

        return Map.of(
                "list", content,
                "pagination", pagination
        );
    }

    @Transactional
    public NoticeDetailResponse getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        Optional<String> loginIdOpt = SecurityUtils.getCurrentUserLoginId();
        loginIdOpt.ifPresent(loginId -> this.markNoticeAsRead(noticeId, loginId));

        boolean hasLiked = loginIdOpt
                .map(loginId -> likeService.hasLiked(loginId, noticeId))
                .orElse(false);

        return NoticeDetailResponse.from(notice, hasLiked); // isRead는 상세 조회에서 필요 시 확장
    }

    @Transactional
    public NoticeDetailResponse updateNotice(Long noticeId, NoticeRequest noticeRequest) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        // 필드 업데이트
        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        notice.setCategory(noticeRequest.getCategory());
        notice.setAuthor(noticeRequest.getAuthor());
        notice.setPinned(noticeRequest.getPinned());
        notice.setStartDate(noticeRequest.getStartDate());
        notice.setEndDate(noticeRequest.getEndDate());
        notice.setAttachmentUrl(noticeRequest.getAttachmentUrl());
        notice.setStatus(noticeRequest.getStatus());

        // 기존 파일 초기화
        notice.getNoticeFiles().forEach(f -> {
            f.setUsedInContent(false);
            f.setIsThumbnail(false);
        });
        fileAttachService.attachFilesFromContent(noticeRequest.getContent(), notice);

        return NoticeDetailResponse.from(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));

        // 연관된 파일도 삭제 (연관관계가 설정되어 있어 orphanRemoval = true 라면 자동 삭제됨)
        noticeRepository.delete(notice);
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