// Service
package com.yeoboya.lunch.api.v1.board.anonymous.service;

import com.yeoboya.lunch.api.v1.board.anonymous.domain.AnonymousBoard;
import com.yeoboya.lunch.api.v1.board.anonymous.repository.AnonymousBoardRepository;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardCreate;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardDelete;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardReport;
import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardUpdate;
import com.yeoboya.lunch.api.v1.board.anonymous.response.AnonymousBoardResponse;
import com.yeoboya.lunch.api.v1.common.response.SlicePagination;
import com.yeoboya.lunch.config.redis.RedisUtil;
import com.yeoboya.lunch.config.util.IPUtils;
import com.yeoboya.lunch.config.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnonymousBoardService {

    private final RedisUtil redisUtil;
    private final AnonymousBoardRepository anonymousBoardRepository;

    public AnonymousBoardResponse create(AnonymousBoardCreate board, String uuid, HttpServletRequest request) {
        String ipHash = IPUtils.getHashedClientIP(request);
        String passwordHash = PasswordUtils.hash(board.getPassword());

        AnonymousBoard anonymousBoard = AnonymousBoard.toEntity(board, ipHash, passwordHash);
        AnonymousBoard save = anonymousBoardRepository.save(anonymousBoard);

        // 🔐 작성자의 clientUUID 기준으로 redis 최신 postId 등록
        if (uuid != null) {
            String redisKey = "anonymous:latest:client:" + uuid;
            redisUtil.setStringOps(redisKey, save.getId().toString(), 30, TimeUnit.MINUTES);
        }

        return AnonymousBoardResponse.from(save);
    }

    @Transactional
    public AnonymousBoardResponse update(AnonymousBoardUpdate anonymousBoardUpdate) {
        AnonymousBoard board = anonymousBoardRepository.findById(anonymousBoardUpdate.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        if (!PasswordUtils.matches(anonymousBoardUpdate.getPassword(), board.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        board.setContent(anonymousBoardUpdate.getContent());
        board.setDeleteAt(anonymousBoardUpdate.getDeleteAt());
        AnonymousBoard save = anonymousBoardRepository.save(board);
        return AnonymousBoardResponse.from(save);
    }

    @Transactional
    public void delete(AnonymousBoardDelete anonymousBoardDelete) {
        AnonymousBoard board = anonymousBoardRepository.findById(anonymousBoardDelete.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        if (!PasswordUtils.matches(anonymousBoardDelete.getPassword(), board.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        anonymousBoardRepository.delete(board);
    }

    @Transactional
    public void report(AnonymousBoardReport anonymousBoardReport) {
        AnonymousBoard board = anonymousBoardRepository.findById(anonymousBoardReport.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));
        log.error("Anonymous Board {} Report Reason : {}", anonymousBoardReport.getBoardId(), anonymousBoardReport.getReason());
        board.setReportCount(board.getReportCount() + 1);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAnonymousBoards(Pageable pageable, @Nullable String clientUUID) {
        Slice<AnonymousBoard> anonymousBoardSlice = anonymousBoardRepository.findAllBySlice(pageable);
        List<AnonymousBoardResponse> anonymousBoardResponses = anonymousBoardSlice.getContent()
                .stream()
                .map(AnonymousBoardResponse::from)
                .collect(Collectors.toList());

        // 최신 게시글 동기화 (첫 페이지일 때만)
        if (pageable.getPageNumber() == 0 && clientUUID != null) {
            Long latestPostId = getLatestPostId();
            if (latestPostId != null) {
                redisUtil.setStringOps("anonymous:latest:client:" + clientUUID, latestPostId.toString(), 30, TimeUnit.MINUTES);
            }
        }

        SlicePagination slicePagination = SlicePagination.builder()
                .page(anonymousBoardSlice.getNumber() + 1)
                .size(anonymousBoardSlice.getSize())
                .numberOfElements(anonymousBoardSlice.getNumberOfElements())
                .isFirst(anonymousBoardSlice.isFirst())
                .isLast(anonymousBoardSlice.isLast())
                .hasNext(anonymousBoardSlice.hasNext())
                .hasPrevious(anonymousBoardSlice.hasPrevious())
                .build();

        return Map.of(
                "list", anonymousBoardResponses,
                "pagination", slicePagination
        );
    }

    //새로운값 확인
    @Transactional(readOnly = true)
    public boolean hasNewPostForClient(String clientUUID) {
        Long latestPostId = this.getLatestPostId();
        if (latestPostId == null) return false;

        String redisKey = buildRedisKey(clientUUID);
        String cachedIdStr = redisUtil.getStringOps(redisKey);
        Long cachedPostId = cachedIdStr != null ? Long.parseLong(cachedIdStr) : null;

        if (cachedPostId == null) {
            redisUtil.setStringOps(redisKey, latestPostId.toString(), 30, TimeUnit.MINUTES);
            return false; // 최초 조회 시 알림 X
        }

        boolean isNewPost = !cachedPostId.equals(latestPostId);
        redisUtil.setStringOps(redisKey, latestPostId.toString(), 30, TimeUnit.MINUTES);

        return isNewPost;
    }

    public Long syncLatestForClient(String clientUUID) {
        Long latestPostId = this.getLatestPostId();
        if (latestPostId != null) {
            redisUtil.setStringOps(buildRedisKey(clientUUID), latestPostId.toString(), 30, TimeUnit.MINUTES);
        }
        return latestPostId;
    }

    private String buildRedisKey(String uuid) {
        log.error("uuid is {}", uuid);
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("clientUUID가 유효하지 않습니다.");
        }
        return "anonymous:latest:client:" + uuid.replaceAll("[^a-zA-Z0-9\\-]", "");
    }

    @Nullable
    @Transactional(readOnly = true)
    public Long getLatestPostId() {
        return anonymousBoardRepository.findTopByOrderByIdDesc()
                .map(AnonymousBoard::getId)
                .orElse(null);
    }
}