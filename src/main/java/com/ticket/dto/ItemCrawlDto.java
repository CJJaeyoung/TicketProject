package com.ticket.dto;

import com.ticket.entity.ItemCrawl;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ItemCrawlDto {
   
   private String mt20id; // 공연 ID (KOPIS의 mt20id 값)
   private String name; // 공연명
   private String startDate;
   private String endDate;
   private String genre;
   private String venue;
   private String posterFileName; // 파일명만
   private String price;
   private String prfruntime;
   private String prfage;
   
   // 기본 포스터 경로 추가
   private static final String BASE_POSTER_URL = "http://www.kopis.or.kr/upload/pfmPoster/PF_PF";
   
   
   public ItemCrawlDto(ItemCrawl itemCrawl) {
      this.mt20id = itemCrawl.getMt20id();
      this.name = itemCrawl.getName();
      this.genre = itemCrawl.getGenre();
      this.startDate = itemCrawl.getStartDate();
      this.endDate = itemCrawl.getEndDate();
      this.venue = itemCrawl.getVenue();
      this.posterFileName = itemCrawl.getPosterFileName();
      this.price = itemCrawl.getPrice();
      this.prfruntime = itemCrawl.getPrfruntime();
      this.prfage = itemCrawl.getPrfage();
   }
   
   public String getPoster() {
      if (this.posterFileName != null && !this.posterFileName.isEmpty()) {
         return BASE_POSTER_URL + this.posterFileName; // 포스터 URL 변환
      }
      return "/images/default-poster.jpg"; // 기본 이미지 설정
   }
   
   // fromEntity()를 static 메서드로 변경
   public static ItemCrawlDto fromEntity(ItemCrawl itemCrawl) {
      if (itemCrawl == null) {
         return null; // itemCrawl이 null이면 null 반환
      }
      return new ItemCrawlDto(itemCrawl);
   }
   
   // List 변환용 fromEntityList() 추가
   public static List<ItemCrawlDto> fromEntityList(List<ItemCrawl> itemCrawlList) {
      if (itemCrawlList == null || itemCrawlList.isEmpty()) {
         return Collections.emptyList();
      }
      return itemCrawlList.stream().map(ItemCrawlDto::fromEntity).toList();
   }
}