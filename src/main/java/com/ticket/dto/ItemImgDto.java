package com.ticket.dto;

import com.ticket.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();
    public static ItemImgDto of(ItemImg itemImg){
        return modelMapper.map(itemImg,ItemImgDto.class);
    }
    
    public static ItemImgDto fromEntity(ItemImg itemImg) {
        ItemImgDto dto = new ItemImgDto();
        dto.setId(itemImg.getId());
        dto.setImgUrl(itemImg.getImgUrl());
        return dto;
    }
}

