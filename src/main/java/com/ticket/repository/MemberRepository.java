package com.ticket.repository;

import com.ticket.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);
    Member findByTel(String tel);
    
    
    Optional<Member> findByNameAndTel(String name, String tel);
    
    Optional<Member> findByPasswordResetToken(String token);

}
