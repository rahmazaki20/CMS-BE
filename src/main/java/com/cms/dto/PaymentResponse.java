package com.cms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(Long id, String description, BigDecimal amount, LocalDate dueDate, LocalDate paidDate,
                              Boolean isPaid) {
}
