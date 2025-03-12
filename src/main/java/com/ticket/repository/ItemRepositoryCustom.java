package com.ticket.repository;

import com.ticket.dto.ItemSearchDto;
import com.ticket.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {
   List<Item> searchItems(ItemSearchDto itemSearchDto);
}