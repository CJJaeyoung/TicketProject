package com.ticket.dto;

import com.ticket.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
   private Long id;
   
   @NotBlank(message = "공연명은 필수 입력 값입니다.")
   private String itemNm;
   
   @NotBlank(message = "가격은 필수 입력 값입니다.")
   private String price;
   
   private String itemDetail;
   
   @NotNull(message = "재고는 필수 입력 값입니다.")
   private Integer stockNumber;
   
   @NotBlank(message = "공연시작일은 필수 입력 값입니다.")
   private String startDate;
   
   @NotBlank(message = "공연종료일 필수 입력 값입니다.")
   private String endDate;
   
   @NotBlank(message = "장르는 필수 입력 값입니다.")
   private String genre;
   
   @NotBlank(message = "공연장소는 필수 입력 값입니다.")
   private String venue;
   
   @NotBlank(message = "관람 시간은 필수 입력 값입니다.")
   private String prfruntime;
   
   @NotBlank(message = "이용가 나이는 필수 입력 값입니다.")
   private String prfage;
   
   private LocalDateTime regTime;
   private LocalDateTime updateTime;
   
   private List<ItemImgDto> itemImgDtoList = new ArrayList<>();
   
   private List<Long> itemImgIds = new ArrayList<>();
   
   private static ModelMapper modelMapper = new ModelMapper();
   
   public Item createItem() {
      return modelMapper.map(this, Item.class);
   }
   
   public static ItemFormDto of(Item item) {
      return modelMapper.map(item, ItemFormDto.class);
   }
   
   private List<ItemImgDto> itemImgs = new ArrayList<>();
   
   
   public static ItemFormDto fromEntity(Item item) {
      ItemFormDto dto = new ItemFormDto();
      dto.setId(item.getId());
      dto.setItemNm(item.getItemNm());
      dto.setPrice(item.getPrice());
      dto.setStockNumber(item.getStockNumber());
      dto.setItemDetail(item.getItemDetail());
      dto.setStartDate(item.getStartDate());
      dto.setEndDate(item.getEndDate());
      dto.setGenre(item.getGenre());
      dto.setVenue(item.getVenue());
      dto.setPrfruntime(item.getPrfruntime());
      dto.setPrfage(item.getPrfage());
      dto.setItemImgs(item.getItemImgs().stream().map(ItemImgDto::fromEntity).toList());
      return dto;
   }
}
