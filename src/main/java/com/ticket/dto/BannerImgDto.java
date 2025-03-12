package com.ticket.dto;

import com.ticket.entity.BannerImg;
import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class BannerImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;

    public static ModelMapper modelMapper = new ModelMapper();


    public static BannerImgDto of(BannerImg bannerImg) {
        return modelMapper.map(bannerImg, BannerImgDto.class);
    }

}
