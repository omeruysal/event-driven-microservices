package com.example.orderservice.saga;

import com.example.core.commands.ReserveProductCommand;
import com.example.core.commands.events.ProductReservedEvent;
import com.example.orderservice.core.events.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Saga
@AllArgsConstructor
public class OrderSaga {
    private transient final CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    //associationProperty should be a property name from the event obj.
    // Because it has to associate the event obj with the saga instance
    public void handle(OrderCreatedEvent orderCreatedEvent){
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: "+ reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if(commandResultMessage.isExceptional()){
                    //Start a compensating transaction
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent){
        //Process user payment
        LOGGER.info("ProductReservedEvent is called for productId: "+ productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());
    }
}
