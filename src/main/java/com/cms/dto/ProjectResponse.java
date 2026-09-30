package com.cms.dto;

import com.cms.entity.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectResponse(
        Long id,
        String projectName,
        String clientName,
        LocalDate createdDate,
        ProjectStatus status,

        /** Base amount / value of work before financial adjustments. */
        BigDecimal baseAmount,

        BigDecimal vatRate,
        BigDecimal vatAmount,

        BigDecimal deduction1Rate,
        BigDecimal deduction1Amount,

        BigDecimal deduction2Rate,
        BigDecimal deduction2Amount,

        BigDecimal retentionRate,
        BigDecimal retentionAmount,

        /** Base amount + VAT, before deductions and retention. */
        BigDecimal grandTotal,

        /** Final amount after VAT, deductions and retention. */
        BigDecimal netPayableAmount,

        BigDecimal remainingBalance,

        List<ProjectItemResponse> items,
        List<PaymentResponse> payments,
        List<DeliveryResponse> deliveries
) {
}
