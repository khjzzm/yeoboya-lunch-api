package com.yeoboya.lunch.api.v1.common.controller.advice;

import com.yeoboya.lunch.api.v1.board.base.constant.BoardSearchType;
import com.yeoboya.lunch.api.v1.common.service.LowerCaseEnumEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class EnumBindingAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BoardSearchType.class, new LowerCaseEnumEditor<>(BoardSearchType.class));
        // 추가적으로 enum이 생기면 아래처럼 등록
        // binder.registerCustomEditor(OtherEnum.class, new LowerCaseEnumEditor<>(OtherEnum.class));
    }
}