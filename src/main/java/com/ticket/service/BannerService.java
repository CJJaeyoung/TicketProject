package com.ticket.service;

import com.ticket.dto.BannerImgDto;
import com.ticket.dto.BannnerFormDto;
import com.ticket.entity.BannerImg;
import com.ticket.entity.Banners;
import com.ticket.repository.BannerImgRepository;
import com.ticket.repository.BannersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BannerService {

    @Value("${bannerLocation}")
    private String bannerLocation;


    private final BannersRepository bannersRepository;
    private final BannerImgRepository bannerImgRepository;


    public void saveBannerFile(BannerImg bannerImg, MultipartFile bannerImgFile) throws Exception {
        String oriImgName = bannerImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if(!StringUtils.isEmpty(oriImgName)) {
            imgName = UUID.randomUUID().toString() + "_" + oriImgName;
            String uploadPath = bannerLocation + "/" + imgName;
            FileUtils.writeByteArrayToFile(new File(uploadPath), bannerImgFile.getBytes());
            imgUrl = "/banner/" + imgName;
        }

        bannerImg.updateBannerImg(oriImgName, imgName, imgUrl);
    }

    // 배너 삭제
    public void deleteBanner(Long bannerId) {
        Banners banners = bannersRepository.findById(bannerId).orElseThrow(EntityNotFoundException::new);
        bannersRepository.delete(banners);
    }


    // 배너 저장
    public Long saveBanner(BannnerFormDto bannnerFormDto, MultipartFile bannerImgFile) throws Exception {
        Banners banners = bannnerFormDto.createBanner();
        bannersRepository.save(banners);

        BannerImg bannerImg = new BannerImg();
        bannerImg.setBanners(banners);
        saveBannerFile(bannerImg, bannerImgFile);
        bannerImgRepository.save(bannerImg);

        return bannnerFormDto.getId();
    }



    @Transactional
    public Long updateBanner(BannnerFormDto bannnerFormDto, MultipartFile bannerImgFile) throws Exception {
        // 배너 엔티티 조회
        Banners banners = bannersRepository.findById(bannnerFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다. id=" + bannnerFormDto.getId()));
        
        // 배너 정보 업데이트
        banners.updateBanner(bannnerFormDto);
        
        // 이미지 파일이 있다면 이미지도 업데이트
        if (!bannerImgFile.isEmpty()) {
            BannerImg savedBannerImg = bannerImgRepository.findByBannersId(banners.getId());
            if (savedBannerImg != null) {
                saveBannerFile(savedBannerImg, bannerImgFile);
            } else {
                BannerImg bannerImg = new BannerImg();
                bannerImg.setBanners(banners);
                saveBannerFile(bannerImg, bannerImgFile);
                bannerImgRepository.save(bannerImg);
            }
        }
        
        return banners.getId();
    }

    // 배너 상세 조회
    @Transactional(readOnly = true)
    public BannnerFormDto getBannerDtl(Long bannerId) {
        Banners banners = bannersRepository.findById(bannerId).orElseThrow(EntityNotFoundException::new);
        BannnerFormDto bannnerFormDto = BannnerFormDto.of(banners);
        
        BannerImg bannerImg = bannerImgRepository.findByBannersId(bannerId);
        BannerImgDto bannerImgDto = BannerImgDto.of(bannerImg);
       
        bannnerFormDto.setBannerImgDto(bannerImgDto);
        return bannnerFormDto;
    }

    @Transactional(readOnly = true)
    public List<Banners> getBannerList() {
        return bannersRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BannnerFormDto> getLatestContents() {
        return bannersRepository.findAll().stream()
            .map(BannnerFormDto::of)
            .collect(Collectors.toList());
    }

}
