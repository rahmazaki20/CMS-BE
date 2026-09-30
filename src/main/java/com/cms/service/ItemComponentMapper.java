package com.cms.service;

import com.cms.dto.ItemComponentResponse;
import com.cms.entity.ItemComponent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemComponentMapper {

    ItemComponentResponse toResponse(ItemComponent component);
}
