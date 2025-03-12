package com.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class TicketingService {
   
   private final RedisTemplate<String, String> redisTemplate;
   private final static String WAITING_QUEUE = "ticket_waiting_list"; // 대기열
   private final static String PROCESSING_QUEUE = "ticket_processing_list"; // 결제 가능 리스트
   private final static int MAX_PROCESSING_USERS = 5; // 최대 입장 가능자
   
   // 사용자 예매 대기열 등록 (중복 방지)
   // 사용자 예매 대기열 등록 (공연별로 따로 관리)
   public Map<String, String> joinWaitingQueue(String userEmail, String performanceId) {
      Map<String, String> response = new HashMap<>();
      
      String waitingQueueKey = "ticket_waiting_list_" + performanceId; // 공연별 대기열
      String processingQueueKey = "ticket_processing_list_" + performanceId; // 공연별 처리 리스트
      
      try {
         Boolean hasPaid = redisTemplate.hasKey("paid_" + userEmail);
         if (Boolean.TRUE.equals(hasPaid)) {
            redisTemplate.opsForList().leftPush(waitingQueueKey, userEmail);
            response.put("message", "이전 결제가 완료되었습니다. 다시 예매 대기열에 추가되었습니다.");
         } else {
            Long queueSize = redisTemplate.opsForList().size(waitingQueueKey);
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(processingQueueKey, userEmail))) {
               response.put("message", "이미 입장 중입니다. 결제 페이지로 이동합니다.");
            } else {
               redisTemplate.opsForList().leftPush(waitingQueueKey, userEmail);
               response.put("message", "확인을 누르면 예매 대기가 시작합니다.");
            }
            response.put("queueSize", String.valueOf(queueSize + 1)); // 대기번호 반환
         }
      } catch (Exception e) {
         System.err.println("Redis 대기열 추가 중 오류 발생: " + e.getMessage());
         response.put("error", "서버 오류로 인해 대기열 추가에 실패했습니다.");
      }
      
      return response;
   }
   
   
   
   
   // 사용자가 결제 가능한지 상태 확인
   // 공연별 결제 가능 상태 확인
   public boolean isProcessing(String userEmail, String performanceId) {
      String processingQueueKey = "ticket_processing_list_" + performanceId; // 공연별 결제 가능 목록
      Boolean isProcessing = redisTemplate.opsForSet().isMember(processingQueueKey, userEmail);
      System.out.println("대기열 상태 확인: " + userEmail + " (공연 " + performanceId + ") - " + (isProcessing ? "입장 가능" : "대기 중"));
      return Boolean.TRUE.equals(isProcessing);
   }
   
   
   
   public boolean completePayment(String userEmail) {
      if (userEmail == null || userEmail.isBlank()) {
         return false;
      }
      
      // 결제 완료된 사용자 Redis에 5분 동안 유지
      redisTemplate.opsForValue().set("paid_" + userEmail, "true", 5, TimeUnit.MINUTES);
      
      // 결제 완료된 사용자 대기열에서 완전히 삭제
      Long removed = redisTemplate.opsForSet().remove(PROCESSING_QUEUE, userEmail);
      redisTemplate.opsForList().remove(WAITING_QUEUE, 0, userEmail);
      
      return removed != null && removed > 0;
   }
   
   
   @Scheduled(fixedRate = 5000) // 5초마다 실행
   public void processWaitingQueue() {
      Set<String> keys = redisTemplate.keys("ticket_waiting_list_*"); // 모든 공연 대기열 조회
      if (keys == null || keys.isEmpty()) {
         return;
      }
      
      for (String waitingQueueKey : keys) {
         String performanceId = waitingQueueKey.replace("ticket_waiting_list_", ""); // 공연 ID 추출
         String processingQueueKey = "ticket_processing_list_" + performanceId;
         
         Long processingSize = redisTemplate.opsForSet().size(processingQueueKey);
         System.out.println("공연 " + performanceId + " 현재 처리 중 사용자 수: " + processingSize);
         
         while (processingSize < MAX_PROCESSING_USERS) {
            String userEmail = redisTemplate.opsForList().rightPop(waitingQueueKey);
            
            if (userEmail == null) {
               System.out.println("공연 " + performanceId + " 대기열 비어 있음. 더 이상 이동할 사용자 없음.");
               break;
            }
            
            // 예매 요청한 사용자만 처리
            Boolean hasRequested = redisTemplate.opsForValue().get("RESERVATION_REQUESTED:" + userEmail) != null;
            
            if (!hasRequested) {
               System.out.println("사용자 " + userEmail + "는 예매 요청을 하지 않음. 대기열에서 제외");
               continue;
            }
            
            System.out.println("사용자 " + userEmail + "가 공연 " + performanceId + " 결제 가능 상태로 변경됨.");
            
            redisTemplate.opsForSet().add(processingQueueKey, userEmail);
            redisTemplate.expire(processingQueueKey, 3, TimeUnit.MINUTES);
            
            processingSize++;
         }
      }
   }
   
   public Map<String, String> requestReservation(String userEmail) {
      if (userEmail == null || userEmail.isEmpty()) {
         return Collections.singletonMap("message", "잘못된 요청");
      }
      
      // Redis에 예매 요청 상태 저장 (10분 유지)
      redisTemplate.opsForValue().set("RESERVATION_REQUESTED:" + userEmail, "true", 10, TimeUnit.MINUTES);
      
      return Collections.singletonMap("message", "예매 요청이 정상적으로 등록되었습니다.");
   }
   
   
   
   private void removeCompletedUsersFromQueue() {
      Set<String> processingUsers = redisTemplate.opsForSet().members(PROCESSING_QUEUE);
      
      for (String userEmail : processingUsers) {
         Boolean hasPaid = redisTemplate.hasKey("paid_" + userEmail);
         
         if (Boolean.TRUE.equals(hasPaid)) {
            redisTemplate.opsForSet().remove(PROCESSING_QUEUE, userEmail);
            redisTemplate.opsForList().remove(WAITING_QUEUE, 0, userEmail);
            redisTemplate.delete("paid_" + userEmail);
            System.out.println(" 결제 완료된 사용자 제거: " + userEmail);
         }
      }
   }
   
   // 현재 대기열 조회 API
   public Long getWaitingQueueSize(String performanceId) {
      String waitingQueueKey = "ticket_waiting_list_" + performanceId; // 공연별 대기열 키
      return redisTemplate.opsForList().size(waitingQueueKey);
   }
}
