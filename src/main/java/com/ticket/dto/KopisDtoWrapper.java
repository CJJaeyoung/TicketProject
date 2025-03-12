package com.ticket.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "dbs") // 최상위 XML 요소
public class KopisDtoWrapper {
   
   @JacksonXmlElementWrapper(useWrapping = false)
   private List<KopisDto> db;  // 리스트로 공연 정보 매핑
   
   public List<KopisDto> getPerformances(){
      return db;
   }
}