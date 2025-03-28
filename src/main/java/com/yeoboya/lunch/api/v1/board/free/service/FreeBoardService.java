package com.yeoboya.lunch.api.v1.board.free.service;

import com.yeoboya.lunch.api.v1.board.free.domain.FreeBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.base.domain.HashTag;
import com.yeoboya.lunch.api.v1.board.free.repository.FreeBoardRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.tag.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.base.repository.reply.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.free.request.BoardCreate;
import com.yeoboya.lunch.api.v1.board.free.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.free.request.BoardSearchCondition;
import com.yeoboya.lunch.api.v1.board.free.response.BoardResponse;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {

    // Repositorieslunch
    private final FreeBoardRepository freeBoardRepository;
    private final HashTagRepository hashTagRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;

    // Services
    private final FileServiceS3 fileService;

    private final FreeBoardReplyService replyService;
    private final FreeBoardLikeService likeService;

    // Others
    private final Response response;

    public ResponseEntity<Body> createFreeBoard(BoardCreate boardCreate) {
        Member member = memberRepository.findByLoginId(boardCreate.getLoginId()).orElseThrow(
                () -> new EntityNotFoundException("Member not found - " + boardCreate.getLoginId()));


        List<BoardHashTag> boardHashtag = Optional.ofNullable(boardCreate.getHashTag())
                .orElse(Collections.emptyList())
                .stream()
                .map(tag -> hashTagRepository.existsHashTagByTag(tag)
                        ? hashTagRepository.findHashTagByTag(tag)
                        : hashTagRepository.save(HashTag.builder().tag(tag).build()))
                .map(BoardHashTag::createBoardHashTag)
                .collect(Collectors.toList());

        FreeBoard freeBoard = FreeBoard.createBoard(member, boardCreate, boardHashtag);
        try {
            FreeBoard save = freeBoardRepository.save(freeBoard);
        } catch (DataAccessException ignored) {

        }

        return response.success(Code.SAVE_SUCCESS);
    }


    @Transactional
    public void updateViewCount(Long noticeId, String loginId) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if (optionalMember.isEmpty()) return; // 비회원이면 아무 작업 안 함

        Member member = optionalMember.get();
        FreeBoard freeBoard = freeBoardRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("FreeBoard not found"));

        freeBoard.setViewCount(freeBoard.getViewCount() + 1);
        freeBoardRepository.save(freeBoard);
    }


    //게시글조회
    public ResponseEntity<Body> list(BoardSearchCondition boardSearchCondition, Pageable pageable) {
        Page<FreeBoard> boards = freeBoardRepository.boardList(boardSearchCondition, pageable);

        List<BoardResponse> boardResponses = boards
                .stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(
                boards.getNumber() + 1,
                boards.isFirst(),
                boards.isLast(),
                boards.isEmpty(),
                boards.getTotalPages(),
                boards.getTotalElements());

        Map<String, Object> data = Map.of(
                "list", boardResponses,
                "pagination", pagination);

        return response.success(Code.SEARCH_SUCCESS, data);
    }

    public ResponseEntity<Body> findBoardById(Long boardId) {
        FreeBoard freeBoard = freeBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found - " + boardId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        BoardResponse boardResponse = BoardResponse.from(
//                freeBoard,
//                replyRepository.findByBoardId(boardId, pageable),
//                likeService.hasLiked(authentication.getName(), boardId)
//        );

        return response.success(Code.SEARCH_SUCCESS, null);
    }

    public ResponseEntity<Body> editBoard(BoardEdit boardEdit) {
        return freeBoardRepository.findById(boardEdit.getBoardId())
                .map(board -> {

                    board.setTitle(boardEdit.getTitle());
                    board.setContent(boardEdit.getContent());
                    board.setPin(boardEdit.getPin());
                    board.setSecret(boardEdit.isSecret());
                    freeBoardRepository.save(board);
                    return response.success(Code.UPDATE_SUCCESS);
                })
                .orElse(response.fail(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public void deleteFreeBoard(Long noticeId) {
        FreeBoard freeBoard = freeBoardRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));

        // 연관된 파일도 삭제 (연관관계가 설정되어 있어 orphanRemoval = true 라면 자동 삭제됨)
        freeBoardRepository.delete(freeBoard);
    }

}
