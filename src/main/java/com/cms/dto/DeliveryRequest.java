package com.cms.dto;

import com.cms.entity.DeliveryType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record DeliveryRequest(@NotNull DeliveryType deliveryType, @NotNull @Positive Integer quantity, String details,
                              LocalDate deliveryDate) {
}
