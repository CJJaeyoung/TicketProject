package com.ticket.controller;

import com.ticket.config.SecurityUtil;
import com.ticket.dto.ItemFormDto;
import com.ticket.entity.Member;
import com.ticket.repository.MemberRepository;
import com.ticket.service.ItemService;
import com.ticket.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ItemController {
   
   @Value("${kakao.map.api-key}")
   private String kakaoApiKey;
   
   @Value("${kakao.rest.api-key}")
   private String kakaoRestApiKey;
   
   private final ItemService itemService;
   private final MemberService memberService;
   private final MemberRepository memberRepository;
   
   @GetMapping(value = "/admin/newitem")
   public String itemForm(Model model) {
      model.addAttribute("itemFormDto", new ItemFormDto());
      return "item/itemForm";
   }
   
   @PostMapping(value = "/admin/newitem")
   public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                         @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
      if (bindingResult.hasErrors()) {
         return "item/itemForm";
      }
      if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
         model.addAttribute("errorMessage",
               "첫번째 상품 이미지는 필수 입력 값입니다.");
         return "item/itemForm";
      }
      try {
         itemService.saveItem(itemFormDto, itemImgFileList);
      } catch (Exception e) {
         model.addAttribute("errorMessage",
               "상품 등록 중 에러가 발생하였습니다.");
         return "item/itemForm";
      }
      return "redirect:/";
   }
   
   @GetMapping(value = "/admin/item/{itemId}")
   public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
      try {
         ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
         model.addAttribute("itemFormDto", itemFormDto);
      } catch (EntityNotFoundException e) {
         model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
         model.addAttribute("itemFormDto", new ItemFormDto());
         return "item/itemForm";
      }
      return "item/itemForm";
   }
   
   @PostMapping(value = "/admin/item/{itemId}")
   public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                            @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                            Model model) {
      if (bindingResult.hasErrors()) {
         return "item/itemForm";
      }
      if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
         model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
         return "item/itemForm";
      }
      try {
         itemService.updateItem(itemFormDto, itemImgFileList);
      } catch (Exception e) {
         model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
         return "item/itemForm";
      }
      return "redirect:/";
   }
   
   
   
   @GetMapping(value = "/admin/itemList")
   public String getItemList(Model model) {
      List<ItemFormDto> items = itemService.getItemList();
      model.addAttribute("items", items);
      return "item/itemList";
   }
   
   @DeleteMapping(value = "/admin/item/{itemId}")
   public ResponseEntity<String> deleteItem(@PathVariable("itemId") Long itemId) {
      try {
         itemService.deleteItem(itemId);
         return ResponseEntity.ok("아이템이 성공적으로 삭제되었습니다.");
      } catch (EntityNotFoundException e) {
         return ResponseEntity.badRequest().body("해당 아이템을 찾을 수 없습니다.");
      } catch (Exception e) {
         return ResponseEntity.internalServerError().body("아이템 삭제 중 오류가 발생했습니다.");
      }
   }
   
   @GetMapping("/item/{itemId}")
   public String itemDetail(@PathVariable Long itemId, Model model,
                            HttpServletResponse response, Principal principal) throws IOException {
      
      final String email;
      final Member member;
      
      if (principal != null) {
         try {
            email = SecurityUtil.getCurrentUserEmail();
            
            member = memberService.findByEmail(email)
                  .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
            
            model.addAttribute("memberId", member.getId());
            
         } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "사용자 정보를 가져오는 중 오류 발생");
            return null;
         }
      } else {
         model.addAttribute("memberId", null);
         email = null;
         member = null;
      }
      
      
      try {
         ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
         model.addAttribute("itemFormDto", itemFormDto);
         model.addAttribute("item", itemFormDto);
         
         // DB나 서비스에서 가격 데이터를 가져온다고 가정
         String rawPriceData = itemFormDto.getPrice(); // 예: "VIP석 110,000원, R석 90,000원, S석 60,000원, A석 40,000원"
         List<Map<String, String>> priceOptions = itemService.parsePriceData(rawPriceData);
         model.addAttribute("priceOptions", priceOptions);
         
         
      } catch (EntityNotFoundException e) {
         model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
         model.addAttribute("itemFormDto", new ItemFormDto());
      }
      
      model.addAttribute("currentUserEmail", email);
      model.addAttribute("currentUserName", (member != null) ? member.getName() : null);
      model.addAttribute("currentUserTel", (member != null) ? member.getTel() : null);

      model.addAttribute("kakaoApiKey", kakaoApiKey);
      model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);
      
      return "item/itemDetail";
   }
   
   
   
}
