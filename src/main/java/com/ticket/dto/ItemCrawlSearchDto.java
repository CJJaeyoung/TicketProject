package com.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCrawlSearchDto {
   
   private String searchBy;
   private String name;
   private String genre;
   private String searchQuery = "";
   
}
