package com.ecommerce.order_service.service;

import java.util.List;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;

public interface IOrderService {
  OrderResponse placeOrder(OrderRequest orderRequest);
  List<OrderResponse> getAllOrders();
  OrderResponse getOrderById(Long id);
  void deleteOrder(Long id);
}
