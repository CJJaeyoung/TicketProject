package com.ticket.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
   
   private String searchBy;
   private String name;
   private String genre;
   private String searchQuery = "";
   
}
