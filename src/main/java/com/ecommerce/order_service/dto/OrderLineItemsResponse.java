package com.ecommerce.order_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsResponse {
    private Long id;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
}