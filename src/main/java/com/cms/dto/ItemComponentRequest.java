package com.cms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemComponentRequest(

        @NotBlank(message = "Component name is required")
        String name,

        @NotNull(message = "Component quantity is required")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "Component quantity must be greater than zero"
        )
        BigDecimal quantity,

        @NotBlank(message = "Component unit is required")
        String unit,

        @NotNull(message = "Component unit price is required")
        @DecimalMin(
                value = "0.00",
                message = "Component unit price cannot be negative"
        )
        BigDecimal unitPrice

) {
}