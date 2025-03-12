package com.ticket.repository;

import com.ticket.entity.ItemCrawl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ItemCrawlRepository extends JpaRepository<ItemCrawl, String>, ItemCrawlRepositoryCustom {
    Optional<ItemCrawl> findBymt20id(String mt20id);
}
