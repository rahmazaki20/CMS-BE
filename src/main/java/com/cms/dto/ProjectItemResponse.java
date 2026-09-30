package com.cms.dto;

import java.math.BigDecimal;

public record ProjectItemResponse(Long id, Long itemId, String itemName, Integer quantity, BigDecimal actualUnitPrice,
                                  BigDecimal totalPrice, String remarks) {
}
