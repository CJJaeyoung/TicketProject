package com.ticket.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticket.dto.MemberSearchDto;
import com.ticket.entity.Member;
import com.ticket.entity.QMember;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class MemberRepositoryImpl implements  MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    
    public MemberRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Member> searchMembers(MemberSearchDto memberSearchDto) {
        QMember member = QMember.member;

        List<Member> results = queryFactory.selectFrom(member)
                .where(searchByNameOrTel(memberSearchDto.getSearchQuery()))
                .orderBy(member.id.desc())
                .fetch();

        // QueryDSL에서 실행된 SQL 확인
        System.out.println("=== QueryDSL 실행된 SQL ===");
        System.out.println(queryFactory
                .selectFrom(member)
                .where(searchByNameOrTel(memberSearchDto.getSearchQuery())) // SQL 변환 확인
                .toString()
        );

        // 검색된 결과 확인
        System.out.println("검색된 결과 수: " + results.size());
        for (Member members : results) {
            System.out.println("이름: " + members.getName() + ", 연락처: " + members.getTel());
        }

        return results;
    }

    // "공연명 OR 장르" 검색 (둘 중 하나라도 포함되면 검색)
    private BooleanExpression searchByNameOrTel(String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            return null; // 검색어가 없으면 조건 제외
        }

        QMember member = QMember.member;

        return member.name.containsIgnoreCase(searchQuery)  // 공연명 검색
                .or(member.tel.containsIgnoreCase(searchQuery)); // 장르 검색
    }
}