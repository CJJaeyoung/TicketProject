package com.ticket.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticket.dto.ItemCrawlSearchDto;
import com.ticket.entity.ItemCrawl;
import com.ticket.entity.QItemCrawl;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class ItemCrawlRepositoryImpl implements ItemCrawlRepositoryCustom {
   private final JPAQueryFactory queryFactory;
   
   // 생성자
   public ItemCrawlRepositoryImpl(EntityManager em) {
      this.queryFactory = new JPAQueryFactory(em);
   }
   
   @Override
   public List<ItemCrawl> searchItemCrawls(ItemCrawlSearchDto itemCrawlSearchDto) {
      QItemCrawl itemCrawl = QItemCrawl.itemCrawl;
      
      List<ItemCrawl> results = queryFactory.selectFrom(itemCrawl)
            .where(searchByNameOrGenre(itemCrawlSearchDto.getSearchQuery())) // "공연명 OR 장르" 검색
            .orderBy(itemCrawl.mt20id.desc())
            .fetch();
      
      // QueryDSL에서 실행된 SQL 확인
      System.out.println("=== QueryDSL 실행된 SQL ===");
      System.out.println(queryFactory
            .selectFrom(itemCrawl)
            .where(searchByNameOrGenre(itemCrawlSearchDto.getSearchQuery())) // SQL 변환 확인
            .toString()
      );
      
      // 검색된 결과 확인
      System.out.println("검색된 결과 수: " + results.size());
      for (ItemCrawl item : results) {
         System.out.println("공연명: " + item.getName() + ", 장르: " + item.getGenre());
      }
      
      return results;
   }
   
   // "공연명 OR 장르" 검색 (둘 중 하나라도 포함되면 검색)
   private BooleanExpression searchByNameOrGenre(String searchQuery) {
      if (!StringUtils.hasText(searchQuery)) {
         return null; // 검색어가 없으면 조건 제외
      }
      
      QItemCrawl itemCrawl = QItemCrawl.itemCrawl;
      
      return itemCrawl.name.containsIgnoreCase(searchQuery)  // 공연명 검색
            .or(itemCrawl.genre.containsIgnoreCase(searchQuery)); // 장르 검색
   }
}
