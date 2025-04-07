package com.yeoboya.lunch.api.v1.board.base.controller;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardType;
import com.yeoboya.lunch.api.v1.board.base.response.BoardTypeResponse;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/types")
public class BoardTypeController {

    private final Response response;

    @GetMapping
    public ResponseEntity<Response.Body> getBoardTypes() {
        List<BoardTypeResponse> result = Arrays.stream(BoardType.values())
                .map(bt -> new BoardTypeResponse(bt.name(), bt.getLabel()))
                .collect(Collectors.toList());

        return response.success(Code.SEARCH_SUCCESS, result);
    }

}