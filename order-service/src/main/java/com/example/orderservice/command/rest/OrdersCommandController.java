package com.example.orderservice.command.rest;

import com.example.orderservice.command.CreateOrderCommand;
import com.example.orderservice.core.OrderStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderDto order){
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .userId("123")
                .ordersStatus(OrderStatus.CREATED)
                .addressId(order.getAddressId())
                .quantity(order.getQuantity())
                .productId(order.getProductId())
                .build();
        String result = commandGateway.sendAndWait(createOrderCommand);

        return result;
    }

}
