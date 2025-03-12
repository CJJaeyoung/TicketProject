package com.ticket.repository;

import com.ticket.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
   
   Optional<Payment> findByImpUid(String impUid);
   
   Page<Payment> findByBuyerEmail(String buyerEmail, Pageable pageable);
   
   default Page<Payment> findByBuyerEmailSorted(String buyerEmail, Pageable pageable) {
      // Pageable에서 페이지 정보 추출
      int page = pageable.getPageNumber();
      int size = pageable.getPageSize();
      
      // 정렬 추가된 Pageable 생성
      Pageable sortedByDateDesc = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
      
      return findByBuyerEmail(buyerEmail, sortedByDateDesc);
      
   }
   
   List<Payment> findByBuyerEmail(String buyerEmail, Sort sort);
   
}
