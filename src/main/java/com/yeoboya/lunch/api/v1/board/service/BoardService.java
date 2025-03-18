package com.yeoboya.lunch.api.v1.board.service;

import com.yeoboya.lunch.api.v1.board.domain.Board;
import com.yeoboya.lunch.api.v1.board.domain.BoardHashTag;
import com.yeoboya.lunch.api.v1.board.domain.HashTag;
import com.yeoboya.lunch.api.v1.board.repository.BoardRepository;
import com.yeoboya.lunch.api.v1.board.repository.HashTagRepository;
import com.yeoboya.lunch.api.v1.board.repository.ReplyRepository;
import com.yeoboya.lunch.api.v1.board.request.BoardCreate;
import com.yeoboya.lunch.api.v1.board.request.BoardEdit;
import com.yeoboya.lunch.api.v1.board.request.BoardSearch;
import com.yeoboya.lunch.api.v1.board.request.FileBoardCreate;
import com.yeoboya.lunch.api.v1.board.response.BoardResponse;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Pagination;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.domain.BoardFile;
import com.yeoboya.lunch.api.v1.file.response.BoardFileResponse;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    // Repositorieslunch
    private final BoardRepository boardRepository;
    private final HashTagRepository hashTagRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;

    // Services
    private final FileServiceS3 fileService;
    private final LikeService likeService;

    // Others
    private final Response response;

    public ResponseEntity<Body> saveBoard(BoardCreate boardCreate) {
        Member member = memberRepository.findByLoginId(boardCreate.getLoginId()).orElseThrow(
                () -> new EntityNotFoundException("Member not found - " + boardCreate.getLoginId()));

        //test 할때 잠시 주석
//        boolean currentUser = SecurityUtils.isCurrentUser(boardCreate.getLoginId());

//        if(!currentUser){
//            return response.fail(ErrorCode.INVALID_AUTH_TOKEN, "정상적인 방법으로 글을 작성해주세요");
//        }

        List<BoardHashTag> boardHashtag = Optional.ofNullable(boardCreate.getHashTag())
                .orElse(Collections.emptyList())
                .stream()
                .map(tag -> hashTagRepository.existsHashTagByTag(tag)
                        ? hashTagRepository.findHashTagByTag(tag)
                        : hashTagRepository.save(HashTag.builder().tag(tag).build()))
                .map(BoardHashTag::createBoardHashTag)
                .collect(Collectors.toList());

        Board board = Board.createBoard(member, boardCreate, boardHashtag);
        try {
            Board save = boardRepository.save(board);
        } catch (DataAccessException ignored) {

        }

        return response.success(Code.SAVE_SUCCESS);
    }


    public ResponseEntity<Body> saveBoardPhoto(MultipartFile file, FileBoardCreate fileBoardCreate) {
        Member member = memberRepository.findByLoginId(fileBoardCreate.getLoginId()).orElseThrow(
                () -> new EntityNotFoundException("Member not found - " + fileBoardCreate.getLoginId()));

        List<BoardHashTag> boardHashtag = Optional.ofNullable(fileBoardCreate.getHashTag())
                .orElse(Collections.emptyList())
                .stream()
                .map(tag -> hashTagRepository.existsHashTagByTag(tag)
                        ? hashTagRepository.findHashTagByTag(tag)
                        : hashTagRepository.save(HashTag.builder().tag(tag).build()))
                .map(BoardHashTag::createBoardHashTag)
                .collect(Collectors.toList());

        Function<FileResponse, BoardFileResponse> responseMapper = BoardFileResponse::apply;
        FileResponse upload = fileService.upload(file, Directory.BOARD, responseMapper);

        BoardFile boardFile = BoardFile.from(upload);

        Board board = Board.createBoard(member, fileBoardCreate, boardHashtag, boardFile);
        boardRepository.save(board);
        return response.success(Code.SAVE_SUCCESS);
    }

    //게시글조회
    public ResponseEntity<Body> list(BoardSearch boardSearch, Pageable pageable) {
        Page<Board> boards = boardRepository.boardList(boardSearch, pageable);

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

    public ResponseEntity<Body> findBoardById(Long boardId, Pageable pageable) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found - " + boardId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        BoardResponse boardResponse = BoardResponse.from(
                board,
                replyRepository.findByBoardId(boardId, pageable),
                likeService.hasLiked(authentication.getName(), boardId)
        );

        return response.success(Code.SEARCH_SUCCESS, boardResponse);
    }

    public ResponseEntity<Body> editBoard(BoardEdit boardEdit, Principal principal) {
        return boardRepository.findById(boardEdit.getBoardId())
                .map(board -> {
                    String loggedInUser = principal.getName();
                    if (!board.getMember().getLoginId().equals(loggedInUser)) {
                        return response.fail(ErrorCode.FORBIDDEN_FAIL);
                    }

                    //fixme 비밀글 관리
//                    if (board.isSecret() && board.getPin() != boardEdit.getPin()) {
//                        return response.fail(ErrorCode.FORBIDDEN_FAIL);
//                    }

                    board.setTitle(boardEdit.getTitle());
                    board.setContent(boardEdit.getContent());
                    board.setPin(boardEdit.getPin());
                    board.setSecret(boardEdit.isSecret());
                    boardRepository.save(board);
                    return response.success(Code.UPDATE_SUCCESS);
                })
                .orElse(response.fail(ErrorCode.NOT_FOUND_FAIL));
    }

}
