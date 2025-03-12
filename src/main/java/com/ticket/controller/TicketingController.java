package com.ticket.controller;

import com.ticket.service.TicketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketingController {
   private final TicketingService ticketingService;
   
   // 예매 대기열 등록 API
   @PostMapping("/join-waiting")
   public ResponseEntity<Map<String, String>> joinWaitingQueue(
         @RequestParam String userEmail,
         @RequestParam String performanceId) {
      
      Map<String, String> response = ticketingService.joinWaitingQueue(userEmail, performanceId);
      return ResponseEntity.ok(response);
   }
   
   
   // 사용자가 결제 가능한지 상태 확인 API
   @GetMapping("/check-status")
   public ResponseEntity<?> checkStatus(
         @RequestParam String userEmail,
         @RequestParam String performanceId) {
      
      try {
         boolean isProcessing = ticketingService.isProcessing(userEmail, performanceId);
         Long queueSize = ticketingService.getWaitingQueueSize(performanceId);
         
         Map<String, Object> response = Map.of(
               "status", isProcessing ? "입장 가능" : "대기 중",
               "queueSize", queueSize
         );
         
         return ResponseEntity.ok(response);
      } catch (Exception e) {
         return ResponseEntity.status(500).body(Collections.singletonMap("error", "상태 확인 실패: " + e.getMessage()));
      }
   }
   
   // 결제 완료 후 대기열에서 제거 API
   @PostMapping("/complete-payment")
   public ResponseEntity<?> completePayment(@RequestParam String userEmail) {
      try {
         if (userEmail == null || userEmail.isBlank()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "유효하지 않은 이메일"));
         }
         
         // 결제 완료 처리 (서비스를 통해 실행)
         boolean removed = ticketingService.completePayment(userEmail);
         
         if (!removed) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "결제 완료 처리 중 오류 발생"));
         }
         
         return ResponseEntity.ok(Collections.singletonMap("message", "결제 완료! 대기열에서 제거됨"));
         
      } catch (Exception e) {
         return ResponseEntity.status(500).body(Collections.singletonMap("error", "결제 완료 실패: " + e.getMessage()));
      }
   }
   
   @PostMapping("/request-reservation")
   public ResponseEntity<Map<String, String>> requestReservation(@RequestParam String userEmail) {
      Map<String, String> response = ticketingService.requestReservation(userEmail);
      return ResponseEntity.ok(response);
   }
   
   
   
   //  현재 대기열 크기 조회 API
   @GetMapping("/waiting-list")
   public ResponseEntity<?> getWaitingQueueSize(@RequestParam String performanceId) {
      Long queueSize = ticketingService.getWaitingQueueSize(performanceId);
      return ResponseEntity.ok(Collections.singletonMap("waitingQueueSize", queueSize));
   }
}
