package com.ticket.entity;

import com.ticket.constant.Role;
import com.ticket.dto.MemberFormDto;
import com.ticket.dto.MemberUpdateFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;

    private String picture;

    private String passwordResetToken;
    private LocalDateTime tokenExpiryDate;


    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.ADMIN);

        return member;
    }

    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    @Builder
    public Member(Long id,String name, String email, String picture, String provider, Role role, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.role = role;
        this.password = password != null ? password : "OAUTH_USER"; // 기본값 설정
    }

    public void updateMember(MemberUpdateFormDto memberUpdateFormDto) {
        this.tel = memberUpdateFormDto.getTel();
    }


}
