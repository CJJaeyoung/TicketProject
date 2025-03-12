package com.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Payment {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @Column(name = "itemNm", nullable = false)
   private String itemNm;
   
   @Column(name = "imp_uid", unique = true, nullable = false)
   private String impUid;
   
   @Column(name = "merchant_uid", unique = true, nullable = false)
   private String merchantUid;
   
   @Column(name = "price", nullable = false)
   private int price;
   
   @Column(name = "performanceDate")
   private String performanceDate;
   
   @Column(name = "status", nullable = false)
   private String status;
   
   @Column(name = "buyer_name", nullable = false)
   private String buyerName;
   
   @Column(name = "buyer_email", nullable = false)
   private String buyerEmail;
   
   @Column(name = "buyer_tel", nullable = false)
   private String buyerTel;
   
   @Column(name = "payment_date", nullable = false)
   private LocalDateTime paymentDate = LocalDateTime.now();
   
   @Column(name = "refundable", nullable = false)
   private boolean refundable = true;
   
   
   
   public Payment(String itemNm, String impUid, String merchantUid, int price, String performanceDate, String status, String buyerName, String buyerEmail, String buyerTel, LocalDateTime paymentDate) {
      this.itemNm = itemNm;
      this.impUid = impUid;
      this.merchantUid = merchantUid;
      this.price = price;
      this.performanceDate = performanceDate;
      this.status = status;
      this.buyerName = buyerName;
      this.buyerEmail = buyerEmail;
      this.buyerTel = buyerTel;
      this.paymentDate = paymentDate;
   }
}
