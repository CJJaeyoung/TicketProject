package com.ticket.repository;

import com.ticket.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
   List<Item> findAll();
}
