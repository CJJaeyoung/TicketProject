package com.ticket.controller;

import com.ticket.config.SecurityUtil;
import com.ticket.dto.MemberFormDto;
import com.ticket.dto.MemberUpdateFormDto;
import com.ticket.dto.SessionUser;
import com.ticket.entity.Member;
import com.ticket.repository.MemberRepository;
import com.ticket.service.MailService;
import com.ticket.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
   private final MemberService memberService;
   private final PasswordEncoder passwordEncoder;
   private final MailService mailService;
   private final HttpSession httpSession;
   private final MemberRepository memberRepository;
   
   String confirm = "";
   boolean confirmCheck = false;
   
   
   @GetMapping(value = "/new")
   public String memberForm(Model model){
      model.addAttribute("memberFormDto",new MemberFormDto());
      return "members/memberForm";
   }
   
   
   @PostMapping(value = "/new")
   public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                            Model model) {
      
      // 1. 입력값 유효성 검사
      if(bindingResult.hasErrors()){
         return "members/memberForm";
      }
      // 2. 이메일 인증 확인
      if(!confirmCheck){
         model.addAttribute("errorMessage","이메일 인증을 하세요.");
         return "members/memberForm";
      }
      // 3. 비밀번호 재확인 검증
      if (!memberFormDto.getPassword().equals(memberFormDto.getConfirmPassword())) {
         model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
         return "members/memberForm";
      }
      try{
         
         Member member = Member.createMember(memberFormDto, passwordEncoder);
         
         memberService.saveMember(member);
      }
      catch (IllegalStateException e){
         model.addAttribute("errorMessage",e.getMessage());
         return "members/memberForm";
      }
      return "redirect:/";
   }
   
   
   
   @GetMapping(value = "/login")
   public String loginForm() {
      return "members/memberLoginForm";
   }
   
   
   @GetMapping(value = "/login/error")
   public String loginError(Model model){
      model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
      return "members/memberLoginForm";
   }
   
   
   @PostMapping("/{email}/emailConfirm")
   public @ResponseBody ResponseEntity emailConfrim(@PathVariable("email") String email)
         throws Exception{
      
      System.out.println(email);
      confirm = mailService.sendSimpleMessage(email);
      return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
   }
   
   
   @PostMapping("/{code}/codeCheck")
   public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code")String code)
         throws Exception{
      if(confirm.equals(code)){
         confirmCheck = true;
         return new ResponseEntity<String>("인증 성공하였습니다.",HttpStatus.OK);
      }
      return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.",HttpStatus.BAD_REQUEST);
   }
   
   
   
   private String getEmailFromPrincipalOrSession(Principal principal) {
      SessionUser user = (SessionUser) httpSession.getAttribute("member");
      if (user != null) {
         return user.getEmail();
      }
      return principal.getName();
   }
   
   @GetMapping("/mypage")
   public String memberMyPage(Model model, Principal principal) {
      String email = SecurityUtil.getCurrentUserEmail();
      
      if (email == null) {
         throw new IllegalStateException("로그인된 사용자의 이메일 정보를 가져올 수 없습니다.");
      }
      
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      System.out.println("Member found: " + member);
      
      MemberUpdateFormDto memberUpdateFormDto = new MemberUpdateFormDto();
      memberUpdateFormDto.setTel(member.getTel());
      
      model.addAttribute("member", member);
      model.addAttribute("memberUpdateFormDto", memberUpdateFormDto);
      
      return "/members/memberMyPage";
   }
   
   @PostMapping(value = "/update/{id}")
   public String memberUpdate(@PathVariable Long id,
                              @Valid MemberUpdateFormDto memberUpdateFormDto,
                              BindingResult bindingResult,
                              Model model,
                              Principal principal) {
      
      if (bindingResult.hasErrors()) {
         model.addAttribute("errorMessage", "입력값에 오류가 있습니다. 다시 확인해주세요.");
         reloadMemberData(model, principal);
         return "/members/memberMyPage";
      }
      
      try {
         memberService.updateMember(memberUpdateFormDto, id);
         model.addAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
      } catch (Exception e) {
         model.addAttribute("errorMessage", "정보 수정 중 에러가 발생하였습니다.");
      }
      
      reloadMemberData(model, principal);
      return "/members/memberMyPage";
   }
   
   
   private void reloadMemberData(Model model, Principal principal) {
      String email = SecurityUtil.getCurrentUserEmail();
      
      if (email == null) {
         throw new IllegalStateException("로그인된 사용자의 이메일 정보를 가져올 수 없습니다.");
      }
      
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      MemberUpdateFormDto memberUpdateFormDto = new MemberUpdateFormDto();
      memberUpdateFormDto.setTel(member.getTel());
      
      model.addAttribute("member", member);
      model.addAttribute("memberUpdateFormDto", memberUpdateFormDto);
   }


   @PostMapping("/change-password")
   public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model, Principal principal) {
      if (principal == null) {
         model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
         reloadMemberData(model, principal);
         return "/members/memberMyPage"; // 전체 페이지 반환
      }
      
      // 소셜 로그인 사용자 비밀번호 변경 막기
      if (isSocialLoginUser(principal)) {
         model.addAttribute("errorMessage", "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
         reloadMemberData(model, principal);
         return "/members/memberMyPage";
      }
      
      // 입력값 검증
      if (currentPassword == null || currentPassword.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
         model.addAttribute("errorMessage", "모든 빈칸을 입력해주세요.");
         reloadMemberData(model, principal);
         return "/members/memberMyPage"; // 전체 페이지 반환
      }
      
      String email = getEmailFromPrincipalOrSession(principal);
      
      try {
         memberService.changePassword(email, currentPassword, newPassword, confirmPassword);
         model.addAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
      } catch (IllegalArgumentException e) {
         model.addAttribute("errorMessage", e.getMessage());
      }
      
      // 필요한 데이터를 다시 로드
      reloadMemberData(model, principal);
      
      return "/members/memberMyPage"; // 전체 페이지 반환
   }


   // 소셜 로그인 사용자 확인 메서드
   private boolean isSocialLoginUser(Principal principal) {
      if (principal instanceof OAuth2AuthenticationToken) {
         return true; // 소셜 로그인 사용자
      }
      return false; // 폼 로그인 사용자
   }


   @GetMapping("/find-email")
   public String showFindEmailPage() {
      return "members/find-email"; // 템플릿 이름
   }


   @GetMapping("/find-password")
   public String showFindPasswordPage() {
      return "members/find-password"; // 정확한 파일 경로
   }


   // 이메일 조회 처리
   @PostMapping("/find-email")
   @ResponseBody
   public ResponseEntity<String> findEmail(@RequestParam("name") String name, @RequestParam("tel") String tel) {
      try {
         // 이메일 찾기 서비스 호출
         String email = memberService.findEmail(name, tel);
         if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body("이메일을 찾을 수 없습니다.");
         }
         return ResponseEntity.ok(email);
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("서버 오류로 인해 이메일 찾기에 실패했습니다.");
      }
   }


   @PostMapping("/find-password")
   @ResponseBody
   public ResponseEntity<String> findPassword(@RequestParam String email) {
      try {
         boolean result = memberService.sendPasswordResetEmail(email);
         if (!result) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 이메일이 없습니다.");
         }
         return ResponseEntity.ok("비밀번호 재설정 이메일이 전송되었습니다.");
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
      }
   }


   @GetMapping("/reset-password")
   public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
      // 로그로 token 값 확인
      System.out.println("Received Token: " + token);

      // 1. 토큰이 null 또는 비어 있는지 확인
      if (token == null || token.trim().isEmpty()) {
         throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
      }

      // 2. 토큰으로 사용자 조회
      Member member = memberRepository.findByPasswordResetToken(token)
              .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

      // 3. 토큰 만료 여부 확인
      if (member.getTokenExpiryDate() == null || member.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
         throw new IllegalArgumentException("토큰이 만료되었습니다.");
      }

      // 4. 모델에 데이터 추가
      model.addAttribute("email", member.getEmail());
      model.addAttribute("token", token);

      // 비밀번호 재설정 페이지 반환
      return "members/reset-password";
   }



   @PostMapping("/reset-password")
   public ResponseEntity<String> resetPassword(
           @RequestParam("token") String token,
           @RequestParam("newPassword") String newPassword,
           @RequestParam("confirmPassword") String confirmPassword) {

      // 로그로 token 값 출력
      System.out.println("Received token: " + token);

      // 1. 토큰이 비어 있는지 확인
      if (token == null || token.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("토큰이 누락되었습니다.");
      }

      // 2. 비밀번호 검증
      if (newPassword == null || newPassword.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("새 비밀번호가 누락되었습니다.");
      }
      if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("비밀번호 확인이 누락되었습니다.");
      }
      if (!newPassword.equals(confirmPassword)) {
         return ResponseEntity.badRequest().body("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      }

      try {
         // 3. 토큰으로 사용자를 조회
         Member member = memberRepository.findByPasswordResetToken(token)
                 .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

         // 4. 토큰 만료 시간 검증
         if (member.getTokenExpiryDate() == null || member.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("토큰이 만료되었습니다.");
         }

         // 5. 비밀번호 변경
         member.setPassword(passwordEncoder.encode(newPassword));

         // 6. 토큰 정보 초기화
         member.setPasswordResetToken(null);
         member.setTokenExpiryDate(null);
         memberRepository.save(member);

         return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");

      } catch (IllegalArgumentException e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      } catch (Exception e) {
         // 예외 처리
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
      }
   }


}
