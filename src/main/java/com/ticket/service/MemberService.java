package com.ticket.service;


import com.ticket.dto.*;
import com.ticket.entity.Member;
import com.ticket.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
   private final MemberRepository memberRepository;
   private final PasswordEncoder passwordEncoder;
   private final MailService mailService;
   
   
   public Long findMemberIdByEmail(String email) {
      return memberRepository.findByEmail(email)
            .map(Member::getId) // Member의 id 반환
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));
   }
   
   public Optional<Member> findById(Long id) {
      return memberRepository.findById(id);
   }
   
   public Optional<Member> findByEmail(String email) {
      return memberRepository.findByEmail(email);
   }
   
   
   public Member saveMember(Member member) {
      validateDuplicateMember(member);
      return memberRepository.save(member);
   }
   
   private void validateDuplicateMember(Member member) {
      // 이메일 중복 확인
      if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
         throw new IllegalStateException("이미 가입된 회원입니다.");
      }
      
      // 전화번호 중복 확인
      if (memberRepository.findByTel(member.getTel()) != null) {
         throw new IllegalStateException("이미 가입된 전화번호입니다.");
      }
   }
   
   public Member findMemberByEmail(String email) {
      return memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
   }
   
   @Override
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      if (member == null) {
         throw new UsernameNotFoundException(email);
      }
      return User.builder().username(member.getEmail())
            .password(member.getPassword())
            .roles(member.getRole().toString())
            .build();
   }
   
   //업데이트
   public void updateMember(MemberUpdateFormDto memberUpdateFormDto, Long id) {
      
      Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
      
      member.updateMember(memberUpdateFormDto);
   }
   

   public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
         throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
      }
      
      if (!newPassword.equals(confirmPassword)) {
         throw new IllegalArgumentException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      }
//      if (newPassword.length() < 8 || !newPassword.matches(".*[A-Za-z].*") || !newPassword.matches(".*[0-9].*")) {
//         throw new IllegalArgumentException("새 비밀번호는 8자 이상이며, 영문과 숫자를 포함해야 합니다.");
//      }
      
      member.setPassword(passwordEncoder.encode(newPassword));
      memberRepository.save(member);
   }
   
   public String findEmail(String name, String tel) {
      Member member = memberRepository.findByNameAndTel(name, tel)
            .orElse(null);
      return member != null ? member.getEmail() : null;
   }
   

   
   // 비밀번호 재설정 링크 생성 (예시)
   private String generatePasswordResetLink(String email) {
      String token = UUID.randomUUID().toString();

      Member member = memberRepository.findByEmail(email)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

      // 토큰 저장
      member.setPasswordResetToken(token);
      member.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // 1시간 만료시간 설정
      memberRepository.save(member);

      return "http://localhost/members/reset-password?token=" + token;
   }

   // 비밀번호 재설정 이메일 전송
   public boolean sendPasswordResetEmail(String email) {
      Member member = memberRepository.findByEmail(email).orElse(null);
      if (member == null) {
         return false; // 이메일이 없는 경우
      }

      String resetLink = generatePasswordResetLink(email);
      String subject = "비밀번호 재설정 요청";
      String message = String.format(
              "<div style='font-family: Arial, sans-serif; color: #333; line-height: 1.6; padding: 20px; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px;'>"
                      + "    <h2 style='color: #00AEEF; text-align: center;'>비밀번호 재설정 요청</h2>"
                      + "    <p>안녕하세요,</p>"
                      + "    <p>비밀번호를 재설정하려면 아래 버튼을 클릭하세요:</p>"
                      + "    <div style='text-align: center; margin: 20px 0;'>"
                      + "        <a href='%s' style='display: inline-block; background-color: #00AEEF; color: #fff; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 16px;'>비밀번호 재설정</a>"
                      + "    </div>"
                      + "    <p>또는 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>"
                      + "    <p style='background-color: #f9f9f9; padding: 10px; border-radius: 5px; word-wrap: break-word; border: 1px solid #ddd;'>%s</p>"
                      + "    <p style='color: #555;'>이 링크는 1시간 동안 유효합니다. 그 이후에는 다시 요청해야 합니다.</p>"
                      + "    <hr style='border: none; border-top: 1px solid #ddd;'>"
                      + "    <p style='font-size: 12px; color: #999; text-align: center;'>"
                      + "        이 이메일은 자동으로 전송되었습니다. 문의 사항이 있으면 고객 지원팀에 연락하세요."
                      + "    </p>"
                      + "</div>",
              resetLink, resetLink);

      mailService.sendMail(email, subject, message);
      return true;
   }


   public List<Member> getAllMembers() {
      return memberRepository.findAll();
   }

   // 검색 기능
   public List<MemberDto> searchMembers(MemberSearchDto searchDto) {
      return memberRepository.searchMembers(searchDto)
              .stream()
              .map(MemberDto::fromEntity) // 엔티티를 DTO로 변환
              .toList();
   }
   
   
   // 특정 회원 정보 조회 (모달에서 AJAX 호출)
   public MemberDto getMemberById(Long id) {
      Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
      return MemberDto.fromEntity(member);  // 정적 메서드 사용
   }
   
   
   // 회원 정보 수정 (모달에서 AJAX 저장)
   public void updateMember(MemberUpdateFormDto memberDto) {
      Member member = memberRepository.findById(memberDto.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
      
      member.setName(memberDto.getName());
      member.setEmail(memberDto.getEmail());
      member.setTel(memberDto.getTel());
      
      memberRepository.save(member);
   }
}


