package com.yeoboya.lunch.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboya.lunch.config.SecretsManagerInitializer;
import com.yeoboya.lunch.config.TestUtil;
import com.yeoboya.lunch.config.security.reqeust.RoleResourcesRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.yeoboya-lunch.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@ContextConfiguration(initializers = SecretsManagerInitializer.class)
class ResourceControllerDocTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    private TestUtil testUtil;

    @BeforeEach
    void setUp() {
        testUtil = new TestUtil(mockMvc, objectMapper);
    }

    @Test
    @DisplayName("리소스 목록 조회")
    void resources() throws Exception {
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("page", "0");
        info.add("size", "10");

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        mockMvc.perform(get("/resource")
                        .params(info)
                        .with(postProcessor)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("resources",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("Page number"),
                                parameterWithName("size").description("Page size")
                        ),
                        responseFields(
                                fieldWithPath("code").description("The response code"),
                                fieldWithPath("message").description("The response message"),
                                fieldWithPath("data").description("The data object"),
                                fieldWithPath("data[].id").description("ID of the resource"),
                                fieldWithPath("data[].resourceName").description("Name of the resource"),
                                fieldWithPath("data[].httpMethod").description("HTTP method for the resource"),
                                fieldWithPath("data[].orderNum").description("Order number of the resource"),
                                fieldWithPath("data[].resourceType").description("Type of the resource"),
                                fieldWithPath("data[].roleDesc").description("Description of the role for the resource")
                        )
                ));
    }

    @Test
    @DisplayName("리소스 추가 및 수정")
    @Transactional
    void addResource() throws Exception {
        RoleResourcesRequest roleResourcesRequest = new RoleResourcesRequest();
//        roleResourcesRequest.setResourceName("/board/**");
//        roleResourcesRequest.setResourceType("url");
//        roleResourcesRequest.setRole(3L);

        RequestPostProcessor postProcessor = testUtil.getToken("admin", "qwer1234@@");

        String jsonResourcesRequest = new ObjectMapper().writeValueAsString(roleResourcesRequest);

        mockMvc.perform(post("/resource/add")
                        .content(jsonResourcesRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(postProcessor)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("resources/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("resourceName").description("Name of the resource to be added"),
                                fieldWithPath("resourceType").description("Type of the resource to be added"),
                                fieldWithPath("role").description("Role for the resource to be added"),
                                fieldWithPath("httpMethod").description("").optional(),
                                fieldWithPath("orderNum").description("").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("The response code"),
                                fieldWithPath("message").description("The response message")
                        )
                ));
    }

}
