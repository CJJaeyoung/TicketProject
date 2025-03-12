package com.ticket.service;


import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
   private final JavaMailSender emailSender;
   public final String ePw = createKey();
   
   private MimeMessage createMessage(String to) throws Exception {
      System.out.println("보내는 대상 : " + to);
      System.out.println("인증 번호 : " + ePw);
      MimeMessage message = emailSender.createMimeMessage();
      
      message.addRecipients(Message.RecipientType.TO, to); // 받는 사람
      message.setSubject("EZTicket. 회원 가입 이메일 인증"); // 제목
      
      // HTML 스타일 적용
      String msgg = "";
      msgg += "<div style='background-color:#f4f4f4; padding:20px; text-align:center; font-family:Verdana, sans-serif;'>";
      msgg += "<div style='max-width:600px; background-color:white; padding:20px; border-radius:10px; ";
      msgg += "box-shadow:0px 0px 10px rgba(0,0,0,0.1); margin:auto; display:block;'>";
      msgg += "<h1 style='color:#00AEEF;'>EZTicket 이메일 인증</h1>";
      msgg += "<p style='font-size:16px; color:#333;'>안녕하세요! 편안한 EZTicket 입니다.</p>";
      msgg += "<p style='font-size:14px; color:#555;'>아래 코드를 복사하여 입력해 주세요.</p>";
      msgg += "<div style='margin:20px auto; padding:15px; border-radius:5px; background-color:#00AEEF; color:white; font-size:22px; font-weight:bold; display:inline-block;'>";
      msgg += ePw + "</div>";
      msgg += "<p style='font-size:14px; color:#777;'>감사합니다.</p>";
      msgg += "</div>";
      msgg += "</div>";
      
      message.setText(msgg, "utf-8", "html"); // 내용
      message.setFrom(new InternetAddress("testotot@naver.com", "EZTicket"));
      
      return message;
   }
   
   
   
   
   public static String createKey() {
      StringBuffer key = new StringBuffer();
      Random r = new Random();
      
      for (int i = 0; i < 8; i++) {
         int index = r.nextInt(3);
         switch (index) {
            case 0:
               key.append((char) ((int) r.nextInt(26) + 97));//a~z
               break;
            case 1:
               key.append((char) ((int) (r.nextInt(26)) + 65));//A~Z
               break;
            case 2:
               key.append(r.nextInt(10));//0~9
               break;
         }
      }
      return key.toString();
      
   }
   
   public String sendSimpleMessage(String to) throws Exception {
      MimeMessage message = createMessage(to);
      try {
         emailSender.send(message);
      } catch (MailException es) {
         es.printStackTrace();
         throw new IllegalArgumentException();
      }
      return ePw;
   }
   
   public void sendMail(String to, String subject, String text) {
      try {
         MimeMessage message = emailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // UTF-8 인코딩 설정
         
         helper.setTo(to);
         helper.setSubject(subject);
         helper.setText(text, true);
         helper.setFrom("testotot@naver.com");
         
         emailSender.send(message);
         System.out.println("Email sent successfully to " + to);
      } catch (Exception e) {
         System.err.println("Error sending email: " + e.getMessage());
         throw new RuntimeException(e);
      }
   }
}
