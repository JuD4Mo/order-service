package com.ecommerce.order_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements IOrderService {
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public OrderResponse placeOrder(OrderRequest orderRequest) {

    log.info("Insertando nuevo pedido");

    Order order = orderMapper.toOrder(orderRequest);

    order.setOrderNumber(UUID.randomUUID().toString());

    Order savedOrder = orderRepository.save(order);

    log.info("Order guardada con éxito. ID: {}", savedOrder.getId());

    return orderMapper.toOrderResponse(savedOrder);
  }

  @Override
  @Transactional(readOnly=true)
  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream()
            .map(orderMapper::toOrderResponse).toList();
  }

  @Override
  @Transactional(readOnly=true)
  public OrderResponse getOrderById(Long id) {
    Order order = orderRepository.findById(id).orElseThrow(
      ()-> new ResourceNotFoundException("Orden","id", id)
    );

    return orderMapper.toOrderResponse(order);
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    if (!orderRepository.existsById(id)){
      throw new ResourceNotFoundException("Orden", "id", id);
    }

    orderRepository.deleteById(id);

    log.info("Orden eliminada con éxito. ID: {}", id);
  }

}
