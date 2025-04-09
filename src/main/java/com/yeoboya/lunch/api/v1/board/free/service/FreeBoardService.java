package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.base.domain.Category;
import com.yeoboya.lunch.api.v1.board.base.request.PasswordCheckRequest;
import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.base.service.CategoryService;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoardFile;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardFileRepository;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardRepository;
import com.yeoboya.lunch.api.v1.board.free.request.FreeBoardCreate;
import com.yeoboya.lunch.api.v1.board.free.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardDetailResponse;
import com.yeoboya.lunch.api.v1.board.free.response.FreeBoardResponse;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.response.FreeBoardFileResponse;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.service.MemberService;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import com.yeoboya.lunch.config.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {

    // repository
    private final FreeBoardFileRepository freeBoardFileRepository;
    private final FreeBoardRepository freeBoardRepository;

    // Services
    private final MemberService memberService;
    private final FreeBoardLikeService likeService;
    private final FreeBoardReplyService replyService;
    private final FreeBoardHashTagService hashTagService;
    private final FreeBoardFileAttachService fileAttachService;
    private final FreeBoardSecretService freeBoardBaseService;
    private final FileServiceS3 fileServiceS3;
    private final CategoryService categoryService;


    @Transactional
    public FreeBoardDetailResponse createFreeBoard(FreeBoardCreate freeBoardCreate) {
        String currentUserLoginId = JwtTokenProvider.getCurrentUserLoginId();
        Member member = memberService.getOptionalMember(currentUserLoginId).orElseThrow(
                () -> new EntityNotFoundException("현재 로그인한 회원 정보를 찾을 수 없습니다."));

        Category category = categoryService.getCategory(freeBoardCreate.getCategoryId());
        List<BoardHashTag> boardHashTags = hashTagService.createBoardHashTags(freeBoardCreate.getHashTag());

        hashTagService.updateHashtagCacheAndScore(freeBoardCreate.getHashTag());

        FreeBoard freeBoard = FreeBoard.createBoard(member, freeBoardCreate, category, boardHashTags);
        FreeBoard saveFreeboard = freeBoardRepository.save(freeBoard);

        fileAttachService.attachFilesFromContent(freeBoardCreate.getContent(), saveFreeboard);
        return FreeBoardDetailResponse.from(saveFreeboard);
    }

    @Transactional
    public FileResponse uploadImage(MultipartFile file) {
        Function<FileResponse, FreeBoardFileResponse> responseMapper = FreeBoardFileResponse::apply;
        FileResponse fileResponse = fileServiceS3.upload(file, Directory.FREE_BOARD, responseMapper);
        FreeBoardFile freeBoardFile = FreeBoardFile.from(fileResponse);
        freeBoardFileRepository.save(freeBoardFile);
        return fileResponse;
    }

    @Transactional
    public void updateViewCount(Long boardNo, String loginId) {
        Optional<Member> optionalMember = memberService.getOptionalMember(loginId);
        if (optionalMember.isEmpty()) return; // 비회원이면 아무 작업 안 함

        Member member = optionalMember.get();
        FreeBoard freeBoard = freeBoardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("FreeBoard not found"));

        freeBoard.setViewCount(freeBoard.getViewCount() + 1);
        freeBoardRepository.save(freeBoard);
    }

    public Map<String, Object> getAllFreeBoards(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        Page<FreeBoardResponse> boards = freeBoardRepository.boardList(boardSearchCondition, pageable);
        List<FreeBoardResponse> content = boards.getContent();

        Pagination pagination = new Pagination(
                boards.getNumber() + 1,
                boards.isFirst(),
                boards.isLast(),
                boards.isEmpty(),
                boards.getTotalPages(),
                boards.getTotalElements());

        return Map.of(
                "list", content,
                "pagination", pagination
        );
    }

    @Transactional
    public FreeBoardDetailResponse getFreeBoardDetail(Long boardId, String pin) {
        FreeBoard freeBoard = freeBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found - " + boardId));

        if (freeBoard.isSecret()) {
            if (!Objects.equals(freeBoard.getPin(), pin)) {
                return FreeBoardDetailResponse.restricted(freeBoard);
            }
        }

        Optional<String> loginIdOpt = SecurityUtils.getCurrentUserLoginId();
        loginIdOpt.ifPresent(loginId -> this.updateViewCount(boardId, loginId));

        boolean mine = loginIdOpt
                .map(loginId -> loginId.equals(freeBoard.getMember().getLoginId()))
                .orElse(false);

        boolean hasLiked = loginIdOpt
                .map(loginId -> likeService.hasLiked(loginId, boardId))
                .orElse(false);

        return FreeBoardDetailResponse.from(freeBoard, hasLiked, mine);
    }


    public ResponseEntity<Response.Body> verifyPassword(PasswordCheckRequest request) {
        return freeBoardBaseService.verifyPassword(request);
    }

    @Transactional
    public FreeBoardDetailResponse editBoard(Long freeBoardId, BoardEdit boardEdit) {
        return freeBoardRepository.findById(freeBoardId)
                .map(board -> {

                    Category category = categoryService.getCategory(boardEdit.getCategoryId());
                    List<BoardHashTag> boardHashTags = hashTagService.createBoardHashTags(boardEdit.getHashTag());

                    // 기존 해시태그 제거
                    board.clearBoardHashTags();

                    // 게시글 필드 수정
                    board.setTitle(boardEdit.getTitle());
                    board.setContent(boardEdit.getContent());
                    if (boardEdit.isSecret()) {
                        board.setSecret(true);
                        board.setPin(boardEdit.getPin()); // 비밀글이면 pin 유지
                    } else {
                        board.setSecret(false);
                        board.setPin(null); // 공개글이면 pin 제거
                    }
                    board.setCategory(category);
                    for (BoardHashTag boardHashTag : boardHashTags) {
                        board.addBoardHashTag(boardHashTag);
                    }

                    hashTagService.updateHashtagCacheAndScore(boardEdit.getHashTag());

                    // 기존 파일 연관관계 초기화
                    board.getFreeBoardFiles().forEach(f -> {
                        f.setUsedInContent(false);
                        f.setIsThumbnail(false);
                    });
                    // 새로운 content에서 파일 재매핑
                    fileAttachService.attachFilesFromContent(boardEdit.getContent(), board);

                    return freeBoardRepository.save(board);
                })
                .map(FreeBoardDetailResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("자유게시판 글을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteFreeBoard(Long boardNo) {
        FreeBoard freeBoard = freeBoardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));

        // 연관된 파일도 삭제 (연관관계가 설정되어 있어 orphanRemoval = true 라면 자동 삭제됨)
        freeBoardRepository.delete(freeBoard);
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

    public ResponseEntity<Response.Body> likePost(Long boardNo) {
        return likeService.likePost(boardNo);
    }

    public ResponseEntity<Response.Body> unlikePost(Long boardNo) {
        return likeService.unlikePost(boardNo);
    }

    @Cacheable(value = "hashtagSearch", key = "#keyword", unless = "#result == null or #result.isEmpty()")
    public ResponseEntity<Response.Body> hashTagSearch(String keyword) {
        return hashTagService.search(keyword);
    }

    public ResponseEntity<Response.Body> getTopHashtagsWithScore(int limit){
        return hashTagService.getTopHashtagsWithScore(limit);
    }

}
