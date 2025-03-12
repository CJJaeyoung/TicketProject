package com.ticket.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticket.constant.Genre;
import com.ticket.dto.ItemSearchDto;
import com.ticket.entity.Item;
import com.ticket.entity.QItem;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
   private final JPAQueryFactory queryFactory;
   
   // 생성자
   public ItemRepositoryImpl(EntityManager em) {
      this.queryFactory = new JPAQueryFactory(em);
   }
   
   @Override
   public List<Item> searchItems(ItemSearchDto itemSearchDto) {
      QItem item = QItem.item;
      
      List<Item> results = queryFactory.selectFrom(item)
            .where(searchByNameOrGenre(itemSearchDto.getSearchQuery())) // "공연명 OR 장르" 검색
            .orderBy(item.id.desc())
            .fetch();
      
      // QueryDSL에서 실행된 SQL 확인
      System.out.println("=== QueryDSL 실행된 SQL ===");
      System.out.println(queryFactory
            .selectFrom(item)
            .where(searchByNameOrGenre(itemSearchDto.getSearchQuery())) // SQL 변환 확인
            .toString()
      );
      
      // 검색된 결과 확인
      System.out.println("검색된 결과 수: " + results.size());
      for (Item result : results) {
         System.out.println("공연명: " + result.getItemNm() + ", 장르: " + result.getGenre());
      }
      
      return results;
   }
   
   // "공연명 OR 장르" 검색 (둘 중 하나라도 포함되면 검색)
   private BooleanExpression searchByNameOrGenre(String searchQuery) {
      if (!StringUtils.hasText(searchQuery)) {
         return null; // 검색어가 없으면 조건 제외
      }
      
      QItem item = QItem.item;
      
      String matchedGenre = findMatchingGenre(searchQuery);
      
      return item.itemNm.containsIgnoreCase(searchQuery)  // 공연명 검색
            .or(matchedGenre != null ? item.genre.eq(matchedGenre) : null);
   }
   
   // 사용자가 입력한 검색어를 바탕으로 Genre Enum 값 찾기
   private String findMatchingGenre(String searchQuery) {
      for (Genre genre : Genre.values()) {
         if (genre.getDisplayName().contains(searchQuery)) {
            return genre.name();
         }
      }
      return null; // 검색어에 해당하는 장르가 없으면 null 반환
   }
}
