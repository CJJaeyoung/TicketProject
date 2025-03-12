package com.ticket.repository;

import com.ticket.dto.ItemCrawlSearchDto;
import com.ticket.entity.ItemCrawl;

import java.util.List;

public interface ItemCrawlRepositoryCustom {
   List<ItemCrawl> searchItemCrawls(ItemCrawlSearchDto itemCrawlSearchDto);
}
