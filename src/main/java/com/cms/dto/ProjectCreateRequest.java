package com.cms.dto;

import com.cms.entity.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectCreateRequest(

        @NotBlank(message = "Project name is required")
        String projectName,

        String clientName,

        LocalDate createdDate,

        ProjectStatus status,

        @DecimalMin(value = "0.00", message = "VAT rate cannot be negative")
        @DecimalMax(value = "100.00", message = "VAT rate cannot exceed 100")
        BigDecimal vatRate,

        @DecimalMin(value = "0.00", message = "Deduction 1 rate cannot be negative")
        @DecimalMax(value = "100.00", message = "Deduction 1 rate cannot exceed 100")
        BigDecimal deduction1Rate,

        @DecimalMin(value = "0.00", message = "Deduction 2 rate cannot be negative")
        @DecimalMax(value = "100.00", message = "Deduction 2 rate cannot exceed 100")
        BigDecimal deduction2Rate,

        @DecimalMin(value = "0.00", message = "Retention rate cannot be negative")
        @DecimalMax(value = "100.00", message = "Retention rate cannot exceed 100")
        BigDecimal retentionRate,

        List<@Valid ProjectItemRequest> items,

        List<@Valid PaymentRequest> payments,

        List<@Valid DeliveryRequest> deliveries
) {
}
