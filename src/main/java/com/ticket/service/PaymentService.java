package com.ticket.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ticket.dto.PaymentDto;
import com.ticket.entity.Member;
import com.ticket.entity.Payment;
import com.ticket.repository.MemberRepository;
import com.ticket.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
   
   @Value("${iamport.api.key}")
   private String IMP_KEY;
   
   @Value("${iamport.api.secret}")
   private String IMP_SECRET;
   
   
   private final PaymentRepository paymentRepository;
   private final MemberRepository memberRepository;
   
   public void savePayment(String itemNm, String impUid, String merchantUid, int price,
                           String performanceDate, String status,
                           String buyerName, String buyerEmail, String buyerTel) {
      
      Payment payment = new Payment(
            itemNm,
            impUid,
            merchantUid,
            price,
            performanceDate,
            status,
            buyerName,
            buyerEmail,
            buyerTel,
            LocalDateTime.now()
      );
      
      paymentRepository.save(payment);
   }
   
   public Page<PaymentDto> getPaymentHistoryByEmail(String email, Pageable pageable) {
      return paymentRepository.findByBuyerEmailSorted(email, pageable)
            .map(payment -> {
               PaymentDto dto = new PaymentDto();
               dto.setId(payment.getId());
               dto.setItemNm(payment.getItemNm());
               dto.setImpUid(payment.getImpUid());
               dto.setMerchantUid(payment.getMerchantUid());
               dto.setPrice(payment.getPrice());
               dto.setPerformanceDate(payment.getPerformanceDate());
               dto.setStatus(payment.getStatus());
               dto.setBuyerName(payment.getBuyerName());
               dto.setBuyerEmail(payment.getBuyerEmail());
               dto.setBuyerTel(payment.getBuyerTel());
               dto.setPaymentDate(payment.getPaymentDate());
               dto.setRefundable(payment.isRefundable()); // 환불 가능 여부 설정
               
               return dto;
            });
   }
   
   
   
   public String processRefundByImpUid(String impUid) {
      Optional<Payment> optionalPayment = paymentRepository.findByImpUid(impUid);
      if (optionalPayment.isEmpty()) {
         return "해당 결제를 찾을 수 없습니다. 결제 ID: " + impUid;
      }
      
      Payment payment = optionalPayment.get();
      
      if (!"paid".equals(payment.getStatus())) {
         return "환불할 수 없는 상태입니다. 현재 상태: " + payment.getStatus();
      }
      
      // 환불 가능 여부 확인
      if (!payment.isRefundable()) {
         return "환불이 불가능한 결제입니다. 결제 ID: " + impUid;
      }
      
      // 포트원 환불 처리 호출
      boolean refundSuccess = requestRefundFromPortOne(payment);
      if (refundSuccess) {
         payment.setStatus("REFUNDED");
         payment.setRefundable(false); // 환불 불가로 설정
         paymentRepository.save(payment);
         
         
         return "환불이 성공적으로 처리되었습니다.";
      }
      
      return "환불 처리에 실패했습니다. 결제 ID: " + impUid;
   }
   
   
   private boolean requestRefundFromPortOne(Payment payment) {
      try {
         // 1. 포트원 액세스 토큰 발급
         String accessToken = getAccessToken();
         if (accessToken == null) {
            System.err.println("[ERROR] 포트원 액세스 토큰 발급 실패");
            return false;
         }
         
         // 2. 환불 요청
         String url = "https://api.iamport.kr/payments/cancel";
         
         // HTTP 요청 설정
         CloseableHttpClient httpClient = HttpClients.createDefault();
         HttpPost httpPost = new HttpPost(url);
         
         JsonObject json = new JsonObject();
         json.addProperty("imp_uid", payment.getImpUid());
         json.addProperty("reason", "사용자 요청에 의한 환불");
         json.addProperty("amount", payment.getPrice());
         
         StringEntity entity = new StringEntity(json.toString(), "UTF-8");
         httpPost.setEntity(entity);
         httpPost.setHeader("Content-Type", "application/json");
         httpPost.setHeader("Authorization", accessToken);
         
         // HTTP 요청 실행
         try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            
            int responseCode = responseJson.get("code").getAsInt();
            if (responseCode != 0) {
               String errorMessage = responseJson.get("message").getAsString();
               System.err.println("[ERROR] Refund failed: " + errorMessage);
               return false;
            }
            
            return true; // 성공 여부 반환
         }
      } catch (Exception e) {
         System.err.println("[ERROR] 환불 처리 중 오류 발생: " + e.getMessage());
         e.printStackTrace();
         return false;
      }
   }
   
   
   private String getAccessToken() throws Exception {
      String url = "https://api.iamport.kr/users/getToken";
      
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpPost httpPost = new HttpPost(url);
      
      JsonObject json = new JsonObject();
      json.addProperty("imp_key", IMP_KEY);
      json.addProperty("imp_secret", IMP_SECRET);
      
      StringEntity entity = new StringEntity(json.toString(), "UTF-8");
      httpPost.setEntity(entity);
      httpPost.setHeader("Content-Type", "application/json");
      
      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
         String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
         JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
         
         if (!responseJson.has("response") || responseJson.get("response").isJsonNull()) {
            System.err.println("[ERROR] 액세스 토큰 응답 데이터가 잘못되었습니다.");
            return null;
         }
         
         JsonObject responseData = responseJson.getAsJsonObject("response");
         if (!responseData.has("access_token") || responseData.get("access_token").isJsonNull()) {
            System.err.println("[ERROR] 액세스 토큰이 응답 데이터에 없습니다.");
            return null;
         }
         
         return responseData.get("access_token").getAsString();
      }
   }
   
   public List<PaymentDto> getPaymentsByMemberId(Long memberId) {
      Optional<Member> optionalMember = memberRepository.findById(memberId);
      if (optionalMember.isEmpty()) {
         return Collections.emptyList();  // 결제 내역이 없으면 빈 리스트 반환
      }
      
      String buyerEmail = optionalMember.get().getEmail();
      List<Payment> payments = paymentRepository.findByBuyerEmail(buyerEmail, Sort.by(Sort.Direction.DESC, "paymentDate"));
      
      
      if (payments.isEmpty()) {
         return Collections.emptyList();  // 결제 내역이 없으면 빈 리스트 반환
      }
      
      return payments.stream().map(payment -> new PaymentDto(
            payment.getId(),
            payment.getItemNm(),
            payment.getImpUid(),
            payment.getMerchantUid(),
            payment.getPrice(),
            payment.getPerformanceDate(),
            payment.getStatus(),
            payment.getBuyerName(),
            payment.getBuyerEmail(),
            payment.getBuyerTel(),
            payment.getPaymentDate(),
            payment.isRefundable()
      )).collect(Collectors.toList());
   }
   
}