package com.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "banner_img")
@Getter
@Setter
public class BannerImg extends BaseEntity {
    @Id
    @Column(name = "banner_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imgName;
    private String oriImgName;
    private String imgUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    private Banners banners;

    public static BannerImg createBannerImg(String oriImgName, String imgName, String imgUrl) {
        BannerImg bannerImg = new BannerImg();
        bannerImg.setOriImgName(oriImgName);
        bannerImg.setImgName(imgName);
        bannerImg.setImgUrl(imgUrl);
        return bannerImg;
    }

    public void updateBannerImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
