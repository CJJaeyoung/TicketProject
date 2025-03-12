package com.ticket.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ticket.dto.ItemCrawlDto;
import com.ticket.dto.ItemCrawlSearchDto;
import com.ticket.dto.KopisDto;
import com.ticket.dto.KopisDtoWrapper;
import com.ticket.entity.ItemCrawl;
import com.ticket.repository.ItemCrawlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class KopisService {
   
   @Value("${kopis.api-key}")
   private String apiKey;
   
   @Value("${kopis.base-poster-url}")
   private String basePosterUrl;
   
   private final WebClient webClient;
   private final ItemCrawlRepository itemCrawlRepository;  // 공연 정보 저장소
   
   
   public List<KopisDto> getHomePagePerformances() {
      LocalDate today = LocalDate.now();
      LocalDate startDate = today.minusYears(1);
      LocalDate endDate = today.plusYears(1);
      
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      String formattedStartDate = startDate.format(formatter);
      String formattedEndDate = endDate.format(formatter);
      
      int rows = 100;
      int page = 1;  // 페이지 번호 기본값 추가
      String keyword = ""; // 검색어 기본값 (비워둠)
      String genre = "";   // 장르 필터 기본값 (비워둠)
      
      // 기존 getPerformances() 메서드를 활용하여 홈 데이터를 가져옴
      return getPerformances(keyword, genre, formattedStartDate, formattedEndDate, rows, page);
   }
   
   // 아이템 기본정보
   public List<KopisDto> getPerformances(String keyword, String genre, String startDate, String endDate, int rows, int page) {
      String url = String.format(
            "https://www.kopis.or.kr/openApi/restful/pblprfr?service=%s&stdate=%s&eddate=%s&rows=%d&cpage=%d",
            apiKey, startDate, endDate, rows, page
      );
      
      if (keyword != null && !keyword.isEmpty()) {
         url += "&prfName=" + keyword;
      }
      
      if (genre != null && !genre.isEmpty()) {
         url += "&shcate=" + genre;
      }
      
      Mono<String> response = webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class);
      
      try {
         String xmlResponse = response.block();
         
         
         XmlMapper xmlMapper = new XmlMapper();
         KopisDtoWrapper wrapper = xmlMapper.readValue(xmlResponse, KopisDtoWrapper.class);
         
         // 공연 정보를 가져온 후 저장하는 로직 추가
         return wrapper.getPerformances().stream()
               .map(this::saveOrUpdatePerformance) // 데이터 저장 추가!
               .collect(Collectors.toList());
         
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Failed to parse XML response", e);
      }
      
   }
   
   
   // 상세페이지 상세조회, 아이템 디테일
   public KopisDto getPerformanceDetail(String mt20id) {
      String url = String.format("https://www.kopis.or.kr/openApi/restful/pblprfr/%s?service=%s", mt20id, apiKey);
      
      System.out.println("KOPIS 상세 API 요청 URL: " + url); // API 요청 URL 확인
      
      Mono<String> response = webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class);
      
      try {
         String xmlResponse = response.block();
         System.out.println("상세 정보 API 응답: " + xmlResponse); // API 응답 확인
         
         XmlMapper xmlMapper = new XmlMapper();
         KopisDtoWrapper wrapper = xmlMapper.readValue(xmlResponse, KopisDtoWrapper.class);
         
         if (wrapper.getPerformances() == null || wrapper.getPerformances().isEmpty()) {
            throw new RuntimeException("API에서 공연 정보를 가져올 수 없습니다.");
         }
         
         KopisDto performance = wrapper.getPerformances().get(0);
         
         return performance;
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Failed to fetch performance detail: " + mt20id, e);
      }
   }
   
   // 공연 정보를 DB에 저장하고, 가격 및 상세 정보 유지하는 메서드
   @Transactional
   private KopisDto saveOrUpdatePerformance(KopisDto dto) {
      Optional<ItemCrawl> existingItemOpt = itemCrawlRepository.findBymt20id(dto.getMt20id());
      
      if (existingItemOpt.isPresent()) {
         // 기존 데이터가 있을 경우 업데이트
         ItemCrawl existingItem = existingItemOpt.get();
         
         existingItem.setGenre(dto.getGenre());
         if (dto.getPrfruntime() != null) existingItem.setPrfruntime(dto.getPrfruntime());
         
         // 기존 데이터에 prfage, price, Prfruntime 가 없으면 상세 API 호출
         if (existingItem.getPrfage() == null || existingItem.getPrice() == null || existingItem.getPrfruntime() == null) {
            System.out.println("상세 정보 업데이트 필요! API 호출 중...: " + dto.getMt20id());
            KopisDto detailedDto = getPerformanceDetail(dto.getMt20id());
            
            existingItem.setPrfage(detailedDto.getPrfage());
            existingItem.setPrice(detailedDto.getPrice());
            existingItem.setPrfruntime(detailedDto.getPrfruntime());
         }
         
         itemCrawlRepository.save(existingItem);
         dto.setPrice(existingItem.getPrice());
         dto.setPrfruntime(existingItem.getPrfruntime());
         dto.setPrfage(existingItem.getPrfage());
         dto.setPoster(basePosterUrl + existingItem.getPosterFileName());
         
      } else {
         // 새로운 공연 정보 저장
         String posterFileName = dto.getPoster().replace(basePosterUrl, "");
         
         // 상세 정보를 한번만 가져와서 저장
         System.out.println("신규 공연 정보 저장! 상세 API 호출 : " + dto.getMt20id());
         KopisDto detailedDto = getPerformanceDetail(dto.getMt20id());
         
         ItemCrawl newItem = ItemCrawl.builder()
               .mt20id(dto.getMt20id())
               .name(dto.getName())
               .startDate(dto.getStartDate())
               .endDate(dto.getEndDate())
               .genre(dto.getGenre())
               .venue(dto.getVenue())
               .posterFileName(posterFileName)
               .price(detailedDto.getPrice()) //
               .prfruntime(dto.getPrfruntime() != null ? dto.getPrfruntime() : "정보 없음")
               .prfage(detailedDto.getPrfage())
               .build();
         
         itemCrawlRepository.save(newItem);
         dto.setPrice(newItem.getPrice());
         dto.setPrfage(newItem.getPrfage());
         dto.setPrfruntime(newItem.getPrfruntime());
         dto.setPoster(basePosterUrl + posterFileName);
      }
      return dto;
   }
   
   // 가격 설정
   public Map<String, Integer> parsePriceOptions(String priceData) {
      Map<String, Integer> priceMap = new LinkedHashMap<>(); // 순서를 유지하려면 LinkedHashMap 사용
      
      String[] priceItems = priceData.split(",(?!\\d)"); // ',' 기준으로 분리
      for (String item : priceItems) {
         item = item.trim(); // 공백 제거
         if (item.isEmpty()) continue;
         
         String[] parts = item.split(" (?=\\d)");
         if (parts.length == 2) {
            try {
               String seatType = parts[0].trim(); // 좌석 타입
               int price = Integer.parseInt(parts[1].replace("원", "").replace(",", "")); // 쉼표 제거 후 변환
               priceMap.put(seatType, price);
            } catch (NumberFormatException e) {
               System.out.println("가격 변환 오류: " + item);
            }
         }
      }
      return priceMap;
   }
   
   
   // 검색 기능
   public List<ItemCrawlDto> searchItemCrawls(ItemCrawlSearchDto searchDto) {
      return itemCrawlRepository.searchItemCrawls(searchDto)
            .stream()
            .map(ItemCrawlDto::fromEntity) // 엔티티를 DTO로 변환
            .toList();
   }
}
