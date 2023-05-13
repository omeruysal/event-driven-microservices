package com.example.orderservice.core.events;

import com.example.orderservice.core.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus ordersStatus;
}
