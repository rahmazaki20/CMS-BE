package com.cms.dto;

import com.cms.entity.DeliveryType;

import java.time.LocalDate;

public record DeliveryResponse(Long id, DeliveryType deliveryType, Integer quantity, String details,
                               LocalDate deliveryDate) {
}
