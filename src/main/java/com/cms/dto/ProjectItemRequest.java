package com.cms.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProjectItemRequest(@NotNull Long itemId, @NotNull @Positive Integer quantity,
                                 @DecimalMin("0.00") BigDecimal actualUnitPrice, String remarks) {
}
