package com.yeoboya.lunch.api.v1.board.free.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import java.util.List;

@Setter
@Getter
public class FileBoardCreate extends BoardCreate {
    private MultipartFile file;

    public FileBoardCreate(@Email String loginId, String title, List<String> hashTag, String content, @Digits(integer = 4, fraction = 0) int pin, boolean secret) {
        super(loginId, title, hashTag, content, pin, secret);
    }

    @Override
    public String toString() {
        return "FileBoardCreate{" +
                "file=" + file + '\'' +
                ", super=" + super.toString() +
                '}';
    }
}
