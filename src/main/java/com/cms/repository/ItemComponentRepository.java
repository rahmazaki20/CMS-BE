package com.cms.repository;

import com.cms.entity.ItemComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemComponentRepository
        extends JpaRepository<ItemComponent, Long> {

    List<ItemComponent> findByItemId(Long itemId);

    Optional<ItemComponent> findByItemIdAndNameIgnoreCase(
            Long itemId,
            String name
    );
}