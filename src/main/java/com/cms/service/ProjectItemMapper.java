package com.cms.service;

import com.cms.dto.ProjectItemResponse;
import com.cms.entity.ProjectItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProjectItemMapper {
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    ProjectItemResponse toResponse(ProjectItem e);
}
