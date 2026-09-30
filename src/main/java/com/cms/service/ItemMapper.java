package com.cms.service;

import com.cms.dto.ItemResponse;
import com.cms.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = ItemComponentMapper.class
)
public interface ItemMapper {

    @Mapping(
            target = "categoryId",
            source = "category.id"
    )
    @Mapping(
            target = "categoryName",
            source = "category.name"
    )
    ItemResponse toResponse(Item e);
}
