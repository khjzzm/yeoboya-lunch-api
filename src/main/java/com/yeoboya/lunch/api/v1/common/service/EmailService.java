package com.yeoboya.lunch.api.v1.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("mailTaskExecutor")
    public void resetPassword(String memberEmail, String authorityLink) {
        MimeMessage message = mailSender.createMimeMessage();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        sb.append("<p>비밀번호를 잊으셨다는 말을 들었습니다. 그럴 수 있죠!</p>");
        sb.append("<p>걱정 마세요! 아래 링크를 통해 비밀번호를 재설정하실 수 있습니다:</p>");
        sb.append("<a href='").append(authorityLink).append("'>비밀번호 재설정하기</a>");
        sb.append("<p>만일 이 링크를 3시간 내에 사용하지 않으면 만료됩니다.</p>");
        sb.append("<p>감사합니다,</p>");
        sb.append("<p>여보야-점심 팀 드림</p>");
        sb.append("</body></html>");
        String body = sb.toString();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setSubject("[Yeoboya-lunch] 비밀번호 재설정");
            messageHelper.setTo(memberEmail);
            messageHelper.setText(body, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
    }

}
