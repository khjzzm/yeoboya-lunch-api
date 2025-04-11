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
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='ko'><head>");
        sb.append("<meta charset='UTF-8'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        sb.append("<style>");
        sb.append("  .container { max-width: 500px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #eaeaea; background-color: #ffffff; }");
        sb.append("  .header { text-align: center; margin-bottom: 30px; }");
        sb.append("  .title { font-size: 20px; font-weight: bold; color: #333333; }");
        sb.append("  .text { font-size: 14px; color: #555555; line-height: 1.6; }");
        sb.append("  .btn-container { text-align: center; }");
        sb.append("  .btn { display: inline-block; padding: 12px 20px; margin-top: 20px; background-color: #1677ff; color: #ffffff !important; text-decoration: none; border-radius: 6px; font-weight: bold; }");
        sb.append("  .footer { font-size: 12px; color: #999999; margin-top: 40px; text-align: center; }");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<div class='container'>");
        sb.append("  <div class='header'>");
        sb.append("    <div class='title'>비밀번호 재설정 안내</div>");
        sb.append("  </div>");
        sb.append("  <div class='text'>");
        sb.append("    <p>비밀번호를 잊으셨다는 말을 들었습니다. 누구나 그럴 수 있죠 😊</p>");
        sb.append("    <p>아래 버튼을 눌러 비밀번호를 재설정해주세요. 링크는 <b>3시간</b> 동안만 유효합니다.</p>");
        sb.append("    <div class='btn-container'>");
        sb.append("      <a href='" + authorityLink + "' class='btn'>비밀번호 재설정하기</a>");
        sb.append("    </div>");
        sb.append("    <p style='margin-top: 20px;'>만약 요청한 적이 없다면, 이 메일은 무시하셔도 됩니다.</p>");
        sb.append("  </div>");
        sb.append("  <div class='footer'>");
        sb.append("    여보야-점심 팀 드림<br/>");
        sb.append("    이 메일은 발신 전용입니다.");
        sb.append("  </div>");
        sb.append("</div>");
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
