package com.cms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ItemRequest(

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotBlank(message = "Item name is required")
        String name,

        @DecimalMin(
                value = "0.00",
                message = "Default unit price cannot be negative"
        )
        BigDecimal defaultUnitPrice,

        @NotBlank(message = "Unit of measure is required")
        String unitOfMeasure,

        List<@Valid ItemComponentRequest> components

) {
}