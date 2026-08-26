package com.ecommerce.order_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import com.ecommerce.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements IOrderService {
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final WebClient.Builder webClientBuilder;

  @Override
  @Transactional
  public OrderResponse placeOrder(OrderRequest orderRequest) {

    log.info("Insertando nuevo pedido");

    List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
            .stream()
            .map(orderMapper::toOrderLineItems)
            .toList();
    
    Order order = new Order();
    order.setOrderNumber(UUID.randomUUID().toString());
    order.setOrderLineItemsList(orderLineItems);

    for (var item: order.getOrderLineItemsList()){
      String sku = item.getSku();
      Integer quantity = item.getQuantity();
      
      Boolean inStock = webClientBuilder.build().get()
              .uri("http://localhost:8082/api/v1/inventory" + sku,
                (uriBuilder) -> uriBuilder.queryParam("quantity", quantity).build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

      if (!Boolean.TRUE.equals(inStock)){
        throw new IllegalArgumentException("No hay stock disponible para el producto: " +sku);
      }
    }

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
