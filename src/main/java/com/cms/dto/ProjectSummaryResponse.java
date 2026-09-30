package com.cms.dto;

import com.cms.entity.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectSummaryResponse(
        Long id,
        String projectName,
        String clientName,
        LocalDate createdDate,
        ProjectStatus status,
        BigDecimal baseAmount,
        BigDecimal vatAmount,
        BigDecimal deduction1Amount,
        BigDecimal deduction2Amount,
        BigDecimal retentionAmount,
        BigDecimal grandTotal,
        BigDecimal netPayableAmount,
        BigDecimal remainingBalance
) {
}
