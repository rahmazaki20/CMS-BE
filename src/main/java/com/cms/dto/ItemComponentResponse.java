package com.cms.dto;

import java.math.BigDecimal;

public record ItemComponentResponse(
        Long id,
        String name,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}