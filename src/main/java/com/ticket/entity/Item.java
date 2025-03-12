package com.ticket.entity;

import com.ticket.dto.ItemFormDto;
import com.ticket.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
   @Id
   @Column(name = "item_id")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @Column(nullable = false, length = 50)
   private String itemNm;
   
   @Column(name = "price", nullable = false)
   private String price;
   
   @Column(nullable = false)
   private int stockNumber;
   
   @Lob
   private String itemDetail;
   
   @Column(nullable = false)
   private String startDate;
   
   @Column(nullable = false)
   private String endDate;
   
   @Column(nullable = false)
   private String genre;
   
   @Column(nullable = false)
   private String venue;
   
   @Column(nullable = false)
   private String prfruntime;
   
   @Column(nullable = false)
   private String prfage;
   
   
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
         name = "member_item",
         joinColumns = @JoinColumn(name = "member_id"),
         inverseJoinColumns = @JoinColumn(name = "item_id")
   )
   private List<Member> member;
   
   @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<ItemImg> itemImgs;
   
   
   public void updateItem(ItemFormDto itemFormDto) {
      this.itemNm = itemFormDto.getItemNm();
      this.price = itemFormDto.getPrice();
      this.stockNumber = itemFormDto.getStockNumber();
      this.itemDetail = itemFormDto.getItemDetail();
      this.startDate = itemFormDto.getStartDate();
      this.endDate = itemFormDto.getEndDate();
      this.genre = itemFormDto.getGenre();
      this.venue = itemFormDto.getVenue();
      this.prfruntime = itemFormDto.getPrfruntime();
      this.prfage = itemFormDto.getPrfage();
   }

   
   public void removeStock(int stockNumber) {
      int restStock = this.stockNumber - stockNumber; // 10,  5 / 10, 20
      if (restStock < 0) {
         throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: " + this.stockNumber + ")");
      }
      this.stockNumber = restStock; // 5
   }
   
   public void addStock(int stockNumber) {
      this.stockNumber += stockNumber;
   }
   
}
