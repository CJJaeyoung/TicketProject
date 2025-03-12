package com.ticket.controller;

import com.ticket.config.SecurityUtil;
import com.ticket.dto.PaymentDto;
import com.ticket.entity.Member;
import com.ticket.repository.MemberRepository;
import com.ticket.repository.PaymentRepository;
import com.ticket.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {
   
   private final PaymentService paymentService;
   private final MemberRepository memberRepository;
   private final PaymentRepository paymentRepository;
   
   @PostMapping("/payment/complete")
   @ResponseBody
   public String completePayment(@RequestBody PaymentDto paymentDto) {
      
      Member member = memberRepository.findByEmail(paymentDto.getBuyerEmail())
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다: " + paymentDto.getBuyerEmail()));
      
      paymentService.savePayment(
            paymentDto.getItemNm(),
            paymentDto.getImpUid(),
            paymentDto.getMerchantUid(),
            paymentDto.getPrice(),
            paymentDto.getPerformanceDate(),
            paymentDto.getStatus(),
            paymentDto.getBuyerName(),
            paymentDto.getBuyerEmail(),
            paymentDto.getBuyerTel()
      );
      
      return "결제 정보가 성공적으로 저장되었습니다.";
   }
   
   @GetMapping("/payment/orderList")
   public String getPaymentHistory(Principal principal, @PageableDefault(size = 10) Pageable pageable, Model model) {
      // 인증된 사용자의 이메일 가져오기
      String email = SecurityUtil.getCurrentUserEmail();
      
      if (email == null) {
         throw new IllegalStateException("로그인된 사용자의 이메일 정보를 가져올 수 없습니다.");
      }
      
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      // 결제 이력 데이터를 서비스에서 가져오기
      Page<PaymentDto> paymentHistory = paymentService.getPaymentHistoryByEmail(email, pageable);
      
      if (paymentHistory.isEmpty()) {
         paymentHistory = new PageImpl<>(new ArrayList<>(), pageable, 1); // 빈 페이지 생성
      }
      
      List<PaymentDto> updatedPayments = paymentHistory.getContent().stream()
            .map(paymentDto -> {
               // paymentDto의 데이터를 사용하여 환불 가능 여부 계산
               boolean isWithin3Days = Duration.between(paymentDto.getPaymentDate(), LocalDateTime.now()).toDays() < 3;
               
               paymentDto.setRefundable(isWithin3Days); // DTO에 환불 가능 여부 설정
               return paymentDto;
            })
            .toList();
      
      // 모델에 데이터 추가
      model.addAttribute("payments", paymentHistory);
      model.addAttribute("email", email);
      
      // HTML 템플릿 반환
      return "payment/orderList";
   }
   
   
   @GetMapping("/payment/success")
   public String paymentSuccess(Model model) {
      // 필요한 데이터 설정 (예: 사용자 정보, 결제 정보)
      model.addAttribute("message", "결제가 성공적으로 완료되었습니다!");
      model.addAttribute("redirectUrl", "/orders/orderdetail"); // 주문 상세 페이지로 이동
      
      return "payment/success"; // HTML 파일 이름
   }
   
   @GetMapping("/payment/failure")
   public String paymentFailure(Model model) {
      model.addAttribute("message", "결제가 실패하였습니다. 다시 시도해주세요.");
      model.addAttribute("retryUrl", "/subscribe/buy"); // 결제 재시도 페이지로 이동
      return "payment/failure"; // 실패 화면 HTML 파일 이름
   }
   
   
   
   @PostMapping("/payment/refund")
   public ResponseEntity<String> refundPayment(@RequestBody Map<String, String> request) {
      try {
         String impUid = request.get("impUid");
         System.out.println("확인용 impUid : " + impUid);
         String result = paymentService.processRefundByImpUid(impUid);
         System.out.println("확인용 result :" + result);
         return ResponseEntity.ok(result);
      } catch (RuntimeException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("환불 처리 중 예상치 못한 오류가 발생했습니다.");
      }
   }
   
}
