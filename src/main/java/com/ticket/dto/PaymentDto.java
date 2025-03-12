package com.ticket.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
   
   private Long id;
   private String itemNm;
   private String impUid;
   private String merchantUid;
   private int price;
   private String performanceDate;
   private String status;
   private String buyerName;
   private String buyerEmail;
   private String buyerTel;
   private LocalDateTime paymentDate;
   private boolean refundable = true;
   
}
