package com.example.orderservice.command;

import com.example.orderservice.core.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier// Axon framework will use this identifier to associate this command with and aggregate obj
    private final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus ordersStatus;
}
