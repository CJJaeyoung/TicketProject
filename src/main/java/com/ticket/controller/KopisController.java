package com.ticket.controller;

import com.ticket.config.SecurityUtil;
import com.ticket.dto.KopisDto;
import com.ticket.entity.Member;
import com.ticket.service.KopisService;
import com.ticket.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/kopis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KopisController {
   
   @Value("${kakao.map.api-key}")
   private String kakaoApiKey;
   
   @Value("${kakao.rest.api-key}")
   private String kakaoRestApiKey;
   
   private final KopisService kopisService;
   private final MemberService memberService;
   
   @GetMapping("/performances")
   @ResponseBody
   public List<KopisDto> getPerformances(
         @RequestParam(required = false) String keyword,
         @RequestParam(required = false) String genre,
         @RequestParam String startDate,
         @RequestParam String endDate,
         @RequestParam(defaultValue = "10") int rows,
         @RequestParam(defaultValue = "1") int page) {
      
      return kopisService.getPerformances(keyword, genre, startDate, endDate, rows, page);
   }
   
   
   
   @GetMapping("/detail/{mt20id}")
   public String showPerformanceDetail(@PathVariable String mt20id, Model model,
                                       HttpServletResponse response, Principal principal) throws IOException {
      
      final String email;
      final Member member;
      
      if (principal != null) {
         try {
            email = SecurityUtil.getCurrentUserEmail();
            
            member = memberService.findByEmail(email)
                  .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
            
            model.addAttribute("memberId", member.getId());
            
         } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "사용자 정보를 가져오는 중 오류 발생");
            return null;
         }
      } else {
         model.addAttribute("memberId", null);
         email = null;
         member = null;
      }
      
      KopisDto performance = kopisService.getPerformanceDetail(mt20id);
      
      if (performance == null) {
         throw new EntityNotFoundException("해당 공연을 찾을 수 없습니다: " + mt20id);
      }
      
      Map<String, Integer> priceOptions = kopisService.parsePriceOptions(performance.getPrice());
      
      model.addAttribute("currentUserEmail", email);
      model.addAttribute("currentUserName", (member != null) ? member.getName() : null);
      model.addAttribute("currentUserTel", (member != null) ? member.getTel() : null);
      
      model.addAttribute("performance", performance);
      model.addAttribute("priceOptions", priceOptions);
      model.addAttribute("kakaoApiKey", kakaoApiKey);
      model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);
      return "item/crawlDetail";
   }
   
}
