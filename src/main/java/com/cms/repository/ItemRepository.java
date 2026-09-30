package com.cms.repository;

import com.cms.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

    List<Item> findByCategoryId(Long categoryId);
}
