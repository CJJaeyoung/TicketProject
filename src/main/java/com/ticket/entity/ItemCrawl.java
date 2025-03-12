package com.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item_crawl")
public class ItemCrawl {
    
    @Id
    @Column(name = "mt20id", nullable = false, unique = true)
    private String mt20id; // 공연 ID (KOPIS의 mt20id 값)
    private String name; // 공연명
    private String startDate;
    private String endDate;
    private String genre;
    private String venue;
    private String posterFileName; // 파일명만
    private String price;
    private String prfruntime;
    private String prfage;
}
