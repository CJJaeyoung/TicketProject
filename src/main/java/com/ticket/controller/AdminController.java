package com.ticket.controller;

import com.ticket.dto.*;
import com.ticket.entity.BannerImg;
import com.ticket.entity.Banners;
import com.ticket.entity.Member;
import com.ticket.repository.BannerImgRepository;
import com.ticket.repository.MemberRepository;
import com.ticket.service.BannerService;
import com.ticket.service.MemberService;
import com.ticket.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class AdminController {
   
   private final BannerService bannerService;
   private final BannerImgRepository bannerImgRepository;
   private final MemberService memberService;
   private final MemberRepository memberRepository;
   private final PaymentService paymentService;
   
   
   // 배너 등록 페이지
   @GetMapping("/admin/banners/new")
   public String bannerForm(Model model) {
      model.addAttribute("bannnerFormDto", new BannnerFormDto());
      return "banner/bannerForm";
   }
   
   // 배너등록
   @PostMapping("/admin/banners/new")
   public String bannerNew(@Valid BannnerFormDto bannnerFormDto, BindingResult bindingResult, @RequestParam("bannerImgFile") MultipartFile bannerImgFile, Model model) {
      if (bindingResult.hasErrors()) {  // 유효성 체크
         return "banner/bannerForm";
      }
      if (bannerImgFile.isEmpty() && bannnerFormDto.getId() == null) {
         model.addAttribute("errorMessage", "배너 이미지는 필수 입력 값입니다.");
         return "banner/bannerForm";
      }
      try {
         bannerService.saveBanner(bannnerFormDto, bannerImgFile);
      } catch (Exception e) {
         e.printStackTrace();
         model.addAttribute("errorMessage", "배너 등록 중 오류가 발생했습니다.");
         return "banner/bannerForm";
      }
      return "redirect:/admin/banners";
   }
   
   // 배너 목록 페이지
   @GetMapping("/admin/banners")
   public String bannerList(Model model) {
      List<Banners> banners = bannerService.getBannerList();
      List<BannnerFormDto> bannerDtos = banners.stream()
            .map(banner -> {
               BannnerFormDto dto = BannnerFormDto.of(banner);
               if (banner.getBannerImg() != null) {
                  System.out.println("이미지 URL 확인: " + banner.getBannerImg().getImgUrl());  // URL 확인
                  dto.setBannerImgDto(BannerImgDto.of(banner.getBannerImg()));
               } else {
                  BannerImg bannerImg = bannerImgRepository.findByBannersId(banner.getId());
                  if (bannerImg != null) {
                     System.out.println("Repository에서 찾은 이미지 URL: " + bannerImg.getImgUrl());  // URL 확인
                     dto.setBannerImgDto(BannerImgDto.of(bannerImg));
                  }
               }
               return dto;
            })
            .collect(Collectors.toList());
      
      model.addAttribute("banners", bannerDtos);
      return "banner/bannerList";
   }
   
   // 배너 삭제
   @DeleteMapping("/admin/banners/{bannerId}")
   @ResponseBody
   public ResponseEntity deleteBanner(@PathVariable("bannerId") Long bannerId) {
      try {
         bannerService.deleteBanner(bannerId);
      } catch (Exception e) {
         e.printStackTrace();
         return new ResponseEntity<>("삭제 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
   }
   
   // 배너 수정
   @PostMapping("/admin/banners/{bannerId}/modify")
   public String bannerUpdate(@Valid BannnerFormDto bannnerFormDto, BindingResult bindingResult, @RequestParam("bannerImgFile") MultipartFile bannerImgFile, Model model) {
      if (bindingResult.hasErrors()) {
         return "banner/bannerForm";
      }
      if (bannerImgFile.isEmpty() && bannnerFormDto.getId() == null) {
         model.addAttribute("errorMessage", "배너 이미지는 필수 입력 값입니다.");
         return "banner/bannerForm";
      }
      try {
         bannerService.updateBanner(bannnerFormDto, bannerImgFile);
      } catch (Exception e) {
         e.printStackTrace();
         model.addAttribute("errorMessage", "배너 수정 중 오류가 발생했습니다.");
         return "banner/bannerForm";
      }
      return "redirect:/admin/banners";
   }
   
   // 배너 수정 페이지
   @GetMapping("/admin/banners/{bannerId}/modify")
   public String bannerModifyForm(@PathVariable("bannerId") Long bannerId, Model model) {
      try {
         BannnerFormDto bannnerFormDto = bannerService.getBannerDtl(bannerId);
         model.addAttribute("bannnerFormDto", bannnerFormDto);
         return "banner/bannerForm";
      } catch (EntityNotFoundException e) {
         model.addAttribute("errorMessage", "존재하지 않는 배너입니다.");
         return "banner/bannerList";
      }
   }
   
   // 회원 정보 리스트
   @GetMapping("/admin/memberlist")
   public String getAllMembers(HttpServletRequest request, HttpServletResponse response,
                               Principal principal, @RequestParam(required = false) String searchQuery,
                               Model model) throws IOException {
      List<Member> members = memberService.getAllMembers();
      model.addAttribute("members", members);
      
      
      if (searchQuery == null) {
         searchQuery = ""; //
      }
      
      List<MemberDto> searchResults = Collections.emptyList();
      
      if (searchQuery.trim().isEmpty()) {
      } else {
         System.out.println("검색어: " + searchQuery);
         MemberSearchDto searchDto = new MemberSearchDto();
         searchDto.setSearchQuery(searchQuery);
         
         searchResults = memberService.searchMembers(searchDto);
         System.out.println("검색된 결과 수: " + searchResults.size());
         
         model.addAttribute("searchResults", searchResults);
         model.addAttribute("searchQuery", searchQuery);
      }
      
      return "members/memberlist";
   }
   
   // 특정 회원 정보 조회 (AJAX 요청)
   @GetMapping("/admin/member/{id}")
   @ResponseBody
   public ResponseEntity<MemberDto> getMember(@PathVariable Long id) {
      MemberDto member = memberService.getMemberById(id);
      return ResponseEntity.ok(member);
   }

   
   // 회원 정보 수정 (AJAX 요청)
   @PostMapping("/admin/member/edit")
   @ResponseBody
   public ResponseEntity<String> editMember(@RequestBody @Valid MemberUpdateFormDto memberDto) {
      try {
         memberService.updateMember(memberDto);
         return ResponseEntity.ok("회원 정보 수정 완료");
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정 중 오류 발생");
      }
   }
   
   @GetMapping("/admin/member/payment/{id}")
   @ResponseBody
   public ResponseEntity<List<PaymentDto>> getPaymentHistory(@PathVariable Long id) {
      List<PaymentDto> payments = paymentService.getPaymentsByMemberId(id);
      
      if (payments.isEmpty()) {
         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 No Content 반환
      }
      
      return ResponseEntity.ok(payments);
   }
   
   
}
