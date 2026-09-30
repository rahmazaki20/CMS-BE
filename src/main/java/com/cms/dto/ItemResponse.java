package com.cms.dto;

import java.math.BigDecimal;
import java.util.List;

public record ItemResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        BigDecimal defaultUnitPrice,
        String unitOfMeasure,
        List<ItemComponentResponse> components
) {}