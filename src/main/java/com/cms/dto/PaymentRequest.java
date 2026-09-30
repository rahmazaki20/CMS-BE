package com.cms.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(String description, @NotNull @DecimalMin("0.00") BigDecimal amount, LocalDate dueDate,
                             LocalDate paidDate, Boolean isPaid) {
}
