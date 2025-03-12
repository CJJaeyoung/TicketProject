package com.ticket.repository;

import com.ticket.entity.BannerImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerImgRepository extends JpaRepository<BannerImg, Long> {

    List<BannerImg> findByBannersIdOrderByIdAsc(Long bannersId);

    BannerImg findByBannersId(Long bannersId);
}