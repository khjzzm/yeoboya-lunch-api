package com.yeoboya.lunch.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboya.lunch.api.v1.board.base.request.ReplyCreateRequest;
import com.yeoboya.lunch.api.v1.board.free.request.*;
import com.yeoboya.lunch.config.SecretsManagerInitializer;
import com.yeoboya.lunch.config.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.yeoboya-lunch.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@ContextConfiguration(initializers = SecretsManagerInitializer.class)
class FreeBaseControllerDocTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

//    @Autowired
//    protected LikeService likeService;

    private TestUtil testUtil;

    private static int unique;

    @BeforeEach
    void setUp() {
        unique = new Random().nextInt(10000);
        testUtil = new TestUtil(mockMvc, objectMapper);
    }


    @Test
    @DisplayName("게시판 작성")
    void saveBoardTest() throws Exception {

        FreeBoardCreate freeBoardCreate = FreeBoardCreate.builder()
                .title("Test board title No is " + unique)
                .hashTag(Arrays.asList("snatch", "clean&jerk", "크로스핏"))
                .secret(false)
                .pin(1234)
                .build();

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        String json = objectMapper.writeValueAsString(freeBoardCreate);

        mockMvc.perform(post("/board/write")
                        .with(user("board"))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .with(postProcessor)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/write",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").description("loginId").type(JsonFieldType.STRING),
                                fieldWithPath("title").description("게시글 제목").type(JsonFieldType.STRING),
                                fieldWithPath("content").description("게시글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("hashTag").description("해시태그").type(JsonFieldType.ARRAY),
                                fieldWithPath("secret").description("게시글 공개여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("pin").description("게시글 비밀번호").type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("code").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    @DisplayName("게시판 수정")
    void updateBoardTest() throws Exception {

        BoardEdit boardEdit = BoardEdit.builder()
                .boardId(1L)
                .title("Test board title No is " + unique + "edit ver.")
                .content("Test content")
                .hashTag(Arrays.asList("snatch", "clean&jerk", "크로스핏", "달리기"))
                .secret(false)
                .pin(1234)
                .build();

        String json = objectMapper.writeValueAsString(boardEdit);

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(patch("/board/edit")
                        .with(user("board"))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .with(postProcessor)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("boardId").description("게시글 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("title").description("게시글 제목").type(JsonFieldType.STRING),
                                fieldWithPath("content").description("게시글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("hashTag").description("해시태그").type(JsonFieldType.ARRAY),
                                fieldWithPath("secret").description("게시글 공개여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("pin").description("게시글 비밀번호").type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("code").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    @DisplayName("게시글 조회")
    void getAllFreeBoardsBoardTest() throws Exception {

        BoardSearchCondition boardSearchCondition = new BoardSearchCondition(); // Fill this object with some test data

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("page", "0");
        info.add("size", "10");

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(get("/board")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .params(info)
                        .with(postProcessor)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("페이지").optional(),
                                parameterWithName("size").description("사이즈").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].boardId").description("게시판 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].title").description("게시판 제목").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].content").description("게시판 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].secret").description("비밀글 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.list[].loginId").description("게시글 작성자 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].name").description("게시글 작성자 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].createDate").description("게시글 작성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].hashTag[]").description("해시태그 배열 - 이 경우 비어있음").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.list[].hashTag[].tag").description("해시태그").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].files").description("첨부 파일").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.list[].files[]").description("첨부 파일 리스트").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.list[].files[].originalFileName").description("원본 파일명").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].files[].fileName").description("저장된 파일명").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].files[].filePath").description("파일 경로").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].files[].extension").description("파일 확장자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].files[].size").description("파일 크기").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.list[].files[].externalForm").description("externalForm").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].files[].url").description("File URL").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies").description("댓글 리스트").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.list[].replies[].replyId").description("댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.list[].replies[].writer").description("댓글 작성자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies[].content").description("댓글 내용").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies[].date").description("댓글 작성일").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies[].childReplies[].parentId").description("대댓글 부모 댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.list[].replies[].childReplies[].replyId").description("대댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.list[].replies[].childReplies[].writer").description("대댓글 작성자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies[].childReplies[].content").description("대댓글 내용").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replies[].childReplies[].date").description("대댓글 작성일").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.list[].replyCount").description("댓글 숫자").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].likeCount").description("좋아요 숫자").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].clickLiked").description("좋아요 클릭 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.first").description("첫 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.last").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.empty").description("페이지 존재 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.totalPages").description("전체 페이지 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.page").description("현재 페이지 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.totalElements").description("전체 게시물 수").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("게시글 작성 (파일첨부)")
    void uploadPhotoTest() throws Exception {
        File fileResource = new ClassPathResource("images/test.jpg").getFile();
        byte[] fileBytes = Files.readAllBytes(fileResource.toPath());
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileBytes);

        FileFreeBoardCreate fileBoardCreate = new FileFreeBoardCreate("admin", "파일업로드 테스트", Arrays.asList("#와플곰", "#이모티콘", "#나타났다"),
                "내용입니다.", 7777, false);

        String json = new ObjectMapper().writeValueAsString(fileBoardCreate);
        MockMultipartFile fileBoardCreateFile = new MockMultipartFile("fileBoardCreate", "",
                "application/json", json.getBytes());

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(multipart("/board/write/photo")
                        .file(file)
                        .file(fileBoardCreateFile)
                        .characterEncoding("utf-8")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .with(postProcessor))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("board/write/photo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("file").description("사진 파일"),
                                partWithName("fileBoardCreate").description("글 정보")
                        ),
                        requestFields(
                                fieldWithPath("loginId").description("loginId").type(JsonFieldType.STRING),
                                fieldWithPath("title").description("게시글 제목").type(JsonFieldType.STRING),
                                fieldWithPath("content").description("게시글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("hashTag").description("해시태그").type(JsonFieldType.ARRAY),
                                fieldWithPath("secret").description("게시글 공개여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("pin").description("게시글 비밀번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("uploadType").description("파일 업로드 형태").optional().type(JsonFieldType.STRING),
                                fieldWithPath("file").description("업로드 파일 데이터").optional().type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("code").description("The response code").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("The response message").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void testFindBoardById() throws Exception {
        Long boardId = 1L;

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(get("/board/{id}", boardId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(postProcessor)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/get",
                        pathParameters(
                                parameterWithName("id").description("The id of the board to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data.boardId").description("게시판 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.title").description("게시판 제목").type(JsonFieldType.STRING),
                                fieldWithPath("data.content").description("게시판 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.secret").description("게시판 비밀글 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.loginId").description("게시판 작성자 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("data.name").description("게시판 작성자 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.createDate").description("게시글 작성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.hashTag[].tag").description("해시태그").type(JsonFieldType.STRING),
                                fieldWithPath("data.files").description("첨부 파일").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.files[]").description("첨부 파일 리스트").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.files[].url").description("파일 경로").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.files[].originalFileName").description("원본 파일명").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.files[].fileName").description("저장된 파일명").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.files[].filePath").description("파일 경로").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.files[].extension").description("파일 확장자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.files[].size").description("파일 크기").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.files[].externalForm").description("externalForm").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[]").description("댓글").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.replies[].replyId").description("댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.replies[].writer").description("댓글 작성자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[].content").description("댓글 내용").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[].date").description("댓글 작성일").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[].childReplies[].parentId").description("대댓글 부모 댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.replies[].childReplies[].replyId").description("대댓글 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("data.replies[].childReplies[].writer").description("대댓글 작성자").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[].childReplies[].content").description("대댓글 내용").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replies[].childReplies[].date").description("대댓글 작성일").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("data.replyCount").description("댓글 숫자").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.likeCount").description("좋아요 숫자").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.clickLiked").description("좋아요 클릭여부").type(JsonFieldType.BOOLEAN)
                        )
                ));
    }

    @Test
    @DisplayName("댓글작성")
    public void createCommentTest() throws Exception {
        ReplyCreateRequest request = new ReplyCreateRequest();
        request.setLoginId("admin");
        request.setBoardId(1L);
        request.setContent("1번글 1번 댓글");

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/board/reply/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .with(postProcessor))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/reply/write",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").description("댓글을 작성하는 회원의 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("boardId").description("댓글이 추가되는 게시글의 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("parentReplyId").description("대댓글인 경우 부모 댓글의 ID").type(JsonFieldType.NUMBER).ignored(),
                                fieldWithPath("content").description("댓글 내용").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("code").description("The response code").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("The response message").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    @DisplayName("대댓글작성")
    public void createCommentnestedCommentTest() throws Exception {
        ReplyCreateRequest request = new ReplyCreateRequest();
        request.setLoginId("admin");
        request.setBoardId(1L);
        request.setParentReplyId(1L);
        request.setContent("1번글 1번 댓글의 댓글 (대댓글)");

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/board/reply/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .with(postProcessor))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/nestedComment/write",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").description("댓글을 작성하는 회원의 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("boardId").description("댓글이 추가되는 게시글의 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("parentReplyId").description("대댓글인 경우 부모 댓글의 ID").type(JsonFieldType.NUMBER).optional(),
                                fieldWithPath("content").description("댓글 내용").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("code").description("The response code").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("The response message").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    @DisplayName("게시물의 댓글 목록 조회")
    public void fetchBoardReplies() throws Exception {
        int page = 0;
        int size = 10;

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(
                        get("/board/reply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("boardId", "1")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .with(postProcessor)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/reply",
                        requestParameters(
                                parameterWithName("boardId").description("The id of the board to retrieve the replies from"),
                                parameterWithName("page").description("The page to retrieve"),
                                parameterWithName("size").description("The number of items to retrieve on one page")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].replyId").description("댓글 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].writer").description("댓글 작성자").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].date").description("댓글 작성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].parentId").description("대댓글 부모 댓글 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].childReplies[].replyId").description("대댓글 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].childReplies[].writer").description("대댓글 작성자").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].content").description("대댓글 내용").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].date").description("대댓글 작성일").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.pagination.first").description("첫 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.last").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.empty").description("페이지 존재 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.totalPages").description("전체 페이지 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.page").description("현재 페이지 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.totalElements").description("전체 게시물 수").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("게시물의 대댓글 목록 조회")
    public void fetchBoardnestedCommentReplies() throws Exception {
        int page = 0;
        int size = 10;

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(
                        get("/board/reply/children")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("parentReplyId", "1")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .with(postProcessor)
                )

                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/nestedComment/reply",
                        requestParameters(
                                parameterWithName("parentReplyId").description("부모 댓글 ID"),
                                parameterWithName("page").description("The page to retrieve"),
                                parameterWithName("size").description("The number of items to retrieve on one page")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].replyId").description("댓글 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].writer").description("댓글 작성자").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].date").description("댓글 작성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].parentId").description("대댓글 부모 댓글 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].childReplies[].replyId").description("대댓글 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.list[].childReplies[].writer").description("대댓글 작성자").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].content").description("대댓글 내용").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.list[].childReplies[].date").description("대댓글 작성일").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.pagination.first").description("첫 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.last").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.empty").description("페이지 존재 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pagination.totalPages").description("전체 페이지 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.page").description("현재 페이지 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pagination.totalElements").description("전체 게시물 수").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("게시글 좋아요")
    public void send_board_like() throws Exception {

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(post("/board/like/{boardId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(postProcessor))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("code")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message")
                                        .type(JsonFieldType.STRING)
                        )
                ));
    }


    @Test
    @Transactional
    @DisplayName("게시글 좋아요 취소")
    @WithMockUser(username = "admin", roles = "USER")
    void deleteItem() throws Exception {

//        likeService.likePost(1L);

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(delete("/board/like/{boardId}", 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .with(postProcessor))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("board/like/delete",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("code")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message")
                                        .type(JsonFieldType.STRING)
                        )
                ));
    }

}
