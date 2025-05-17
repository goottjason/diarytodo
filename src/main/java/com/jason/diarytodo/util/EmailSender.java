package com.jason.diarytodo.util;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import jakarta.mail.Transport;
import java.util.Properties;

@Slf4j
@Service
public class EmailSender {
  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  public void sendAuthCodeEmail(String email, String authCode) throws MessagingException {
    String subject = "diarytodo의 회원가입 이메일 인증번호입니다.";
    String message = "회원가입을 환경합니다. [ " + authCode + " ]를 입력하시고, 가입을 완료해주세요.";

    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.naver.com");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.ssl.trust", "smtp.naver.com");

    Session emailSession = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    MimeMessage mime = new MimeMessage(emailSession);
    mime.setFrom(new InternetAddress(username)); // 주입된 username 사용
    mime.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
    mime.setSubject(subject);
    mime.setText(message);

    log.info("{}", mime);
    Transport.send(mime);
  }
}
