package com.ticket.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticket.entity.ItemCrawl;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "db") // XML 최상위 요소
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisDto {
   
   @JacksonXmlProperty(localName = "mt20id")
   private String mt20id;  // 공연 ID
   
   @JacksonXmlProperty(localName = "prfnm")
   private String name;  // 공연명
   
   @JacksonXmlProperty(localName = "prfpdfrom")
   private String startDate;  // 공연 시작일
   
   @JacksonXmlProperty(localName = "prfpdto")
   private String endDate;  // 공연 종료일
   
   @JacksonXmlProperty(localName = "poster")
   private String poster;  // 포스터 이미지 URL
   
   @JacksonXmlProperty(localName = "genrenm")
   private String genre;  // 장르명
   
   @JacksonXmlProperty(localName = "fcltynm")
   private String venue;  // 공연장 이름
   
   @JacksonXmlProperty(localName = "pcseguidance")
   private String price;  // 가격 정보
   
   @JacksonXmlProperty(localName = "prfruntime")
   private String prfruntime;  // 공연 시간
   
   @JacksonXmlProperty(localName = "prfage")
   private String prfage;  // 연령 제한
   
   @JacksonXmlElementWrapper(localName = "styurls")  // XML에서 styurls를 리스트로 인식
   @JacksonXmlProperty(localName = "styurl")  // XML 내부 요소
   private List<String> styurls;  // 여러 개의 styurl을 저장하는 리스트
   
   
   
   
   public KopisDto() {}
   
   public KopisDto(ItemCrawl item) {
      this.mt20id = item.getMt20id();
      this.name = item.getName();
      this.genre = item.getGenre();
      this.startDate = item.getStartDate();
      this.endDate = item.getEndDate();
      this.venue = item.getVenue();
      if (item.getPosterFileName() != null && !item.getPosterFileName().isEmpty()) {
         this.poster = "http://www.kopis.or.kr/upload/pfmPoster/PF_PF" + item.getPosterFileName();  // 🔹 수정된 URL 조합
      }
      this.price = item.getPrice();
      this.prfruntime = item.getPrfruntime();
      this.prfage = item.getPrfage();



   }
}
