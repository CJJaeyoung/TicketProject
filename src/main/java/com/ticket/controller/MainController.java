package com.ticket.controller;

import com.ticket.config.SecurityUtil;
import com.ticket.dto.*;
import com.ticket.entity.Item;
import com.ticket.entity.Member;
import com.ticket.service.BannerService;
import com.ticket.service.ItemService;
import com.ticket.service.KopisService;
import com.ticket.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    private final ItemService itemService;
    private final MemberService memberService;
    private final BannerService bannerService;
    private final KopisService kopisService;


    @GetMapping(value = "/")
    public String main(HttpServletRequest request,HttpServletResponse response,
                       Principal principal, @RequestParam(required = false) String searchQuery,
                       Model model) throws IOException {


        // 현재 페이지 경로 추가
        String currentPath = request.getRequestURI();
        model.addAttribute("currentPath", currentPath);

        // 로그인 여부 확인 후 사용자 정보 추가
        if (principal != null) {
            try {
                Member member = getLoggedInMember(principal);
                model.addAttribute("memberId", member.getId());
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "사용자 정보를 가져오는 중 오류 발생");
                return null;
            }
        } else {
            model.addAttribute("memberId", null); // 로그인하지 않은 경우 memberId를 null로 설정
        }

        List<BannnerFormDto> latestContents = bannerService.getLatestContents().stream()
                .limit(5)
                .toList();
        model.addAttribute("latestContents", latestContents);

        List<KopisDto> performances = kopisService.getHomePagePerformances();
        List<Item> items = itemService.getAllItems();
        
        model.addAttribute("performances", performances);
        model.addAttribute("items", items);

        if (searchQuery == null) {
            searchQuery = ""; //
        }
        
        List<ItemCrawlDto> searchResults = new ArrayList<>();
        List<ItemFormDto> dbResults = new ArrayList<>();
        
        if (!searchQuery.trim().isEmpty()) {
            System.out.println("검색어: " + searchQuery);
            
            // DB 검색
            ItemSearchDto dbSearchDto = new ItemSearchDto();
            dbSearchDto.setSearchQuery(searchQuery);
            dbResults = itemService.searchItems(dbSearchDto);
            System.out.println("DB 검색 결과 수: " + dbResults.size());
            
            // 외부 API (크롤링) 검색
            ItemCrawlSearchDto searchDto = new ItemCrawlSearchDto();
            searchDto.setSearchQuery(searchQuery);
            searchResults = kopisService.searchItemCrawls(searchDto);
            System.out.println("크롤링 검색 결과 수: " + searchResults.size());
        }
        
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("dbResults", dbResults);
        model.addAttribute("searchQuery", searchQuery);
        
        
        return "main";
    }


    private Member getLoggedInMember(Principal principal) {
        String email = SecurityUtil.getCurrentUserEmail();
        return memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

}
