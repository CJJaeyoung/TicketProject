package com.ticket.service;

import com.ticket.dto.*;
import com.ticket.entity.Item;
import com.ticket.entity.ItemCrawl;
import com.ticket.entity.ItemImg;
import com.ticket.repository.ItemCrawlRepository;
import com.ticket.repository.ItemImgRepository;
import com.ticket.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
   
   
   private final ItemCrawlRepository itemCrawlRepository;
   private final ItemRepository itemRepository;
   private final ItemImgService itemImgService;
   private final ItemImgRepository itemImgRepository;
   
   @Transactional(readOnly = true)
   public ItemCrawlDto getCrawlItemDtl(String itemCrawlId) {
      ItemCrawl itemCrawl = itemCrawlRepository.findById(itemCrawlId)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID의 공연 정보를 찾을 수 없습니다: " + itemCrawlId));
      
      return new ItemCrawlDto(itemCrawl);
   }
   
   public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
      Item item = itemFormDto.createItem();
      itemRepository.save(item);
      
      if (itemImgFileList == null || itemImgFileList.isEmpty()) {
         return item.getId();
      }
      
      boolean hasValidImage = false;
      for (int i = 0; i < itemImgFileList.size(); i++) {
         MultipartFile imgFile = itemImgFileList.get(i);
         
         if (imgFile == null || imgFile.isEmpty()) {
            continue;
         }
         
         hasValidImage = true;
         
         ItemImg itemImg = new ItemImg();
         itemImg.setItem(item);
         itemImg.setRepImgYn(i == 0 ? "Y" : "N");
         
         itemImgService.saveItemImg(itemImg, imgFile);
      }
      
      if (!hasValidImage) {
         itemRepository.delete(item);
         throw new IllegalStateException("이미지가 없으면 상품을 등록할 수 없습니다.");
      }
      
      return item.getId();
   }
   
   
   @Transactional(readOnly = true)
   public ItemFormDto getItemDtl(Long itemId) {
      List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
      List<ItemImgDto> itemImgDtoList = itemImgList.stream()
            .map(ItemImgDto::of)
            .collect(Collectors.toList());
      
      Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("해당 아이템을 찾을 수 없습니다."));
      ItemFormDto itemFormDto = ItemFormDto.of(item);
      itemFormDto.setItemImgDtoList(itemImgDtoList);
      
      return itemFormDto;
   }
   
   
   
   public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
         throws Exception {
      //상품 변경
      Item item = itemRepository.findById(itemFormDto.getId()).
            orElseThrow(EntityNotFoundException::new);
      item.updateItem(itemFormDto);
      //상품 이미지 변경
      List<Long> itemImgIds = itemFormDto.getItemImgIds();
      
      for (int i = 0; i < itemImgFileList.size(); i++) {
         itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
      }
      return item.getId();
   }
   
   @Transactional(readOnly = true)
   public List<ItemFormDto> getItemList() {
      List<Item> items = itemRepository.findAll();
      
      return items.stream().map(item -> {
         ItemFormDto itemFormDto = ItemFormDto.of(item);
         
         List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(item.getId());
         
         List<ItemImgDto> itemImgDtoList = itemImgList.stream()
               .map(ItemImgDto::of)
               .collect(Collectors.toList());
         
         itemFormDto.setItemImgDtoList(itemImgDtoList);
         
         return itemFormDto;
      }).collect(Collectors.toList());
   }
   
   
   public void deleteItem(Long itemId) {
      Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("해당 아이템을 찾을 수 없습니다: " + itemId));
      itemRepository.delete(item);
   }
   
   
   public List<Map<String, String>> parsePriceData(String rawPriceData) {
      List<Map<String, String>> priceList = new ArrayList<>();
      if (rawPriceData == null || rawPriceData.isBlank()) {
         return priceList;
      }
      
      // 가격 정보 구분 (", " 기준으로 분리)
      String[] priceEntries = rawPriceData.split(", ");
      
      for (String entry : priceEntries) {
         // "VIP석 110,000원" 같은 데이터를 "VIP석" / "110,000원" 으로 분리
         String[] parts = entry.split(" ");
         if (parts.length == 2) {
            Map<String, String> priceMap = new HashMap<>();
            priceMap.put("seatType", parts[0]); // 좌석 종류
            priceMap.put("price", parts[1]);    // 가격
            priceList.add(priceMap);
         }
      }
      
      return priceList;
   }
   
   public List<Item> getAllItems() {
      return itemRepository.findAll();
   }
   
   //검색기능
   public List<ItemFormDto> searchItems(ItemSearchDto searchDto) {
      return itemRepository.searchItems(searchDto)
            .stream()
            .map(ItemFormDto::fromEntity) // 엔티티를 DTO로 변환
            .toList();
   }
}
