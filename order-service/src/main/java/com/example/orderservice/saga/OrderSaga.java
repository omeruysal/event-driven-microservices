package com.example.orderservice.saga;

import com.example.core.commands.ProcessPaymentCommand;
import com.example.core.commands.commands.CancelProductReservationCommand;
import com.example.core.commands.commands.ReserveProductCommand;
import com.example.core.commands.data.User;
import com.example.core.commands.events.PaymentProcessedEvent;
import com.example.core.commands.events.ProductReservationCancelledEvent;
import com.example.core.commands.events.ProductReservedEvent;
import com.example.core.commands.query.FetchUserPaymentDetailsQuery;
import com.example.orderservice.command.commands.ApproveOrderCommand;
import com.example.orderservice.command.commands.RejectOrderCommand;
import com.example.orderservice.core.events.OrderApprovedEvent;
import com.example.orderservice.core.events.OrderCreatedEvent;
import com.example.orderservice.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;//to be able to send query

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    //associationProperty should be a property name from the event obj.
    // Because it has to associate the event obj with the saga instance
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        // it triggers Product Aggregate to update product
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    //Start a compensating transaction
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId") // it is triggered by Product Aggregate class
    public void handle(ProductReservedEvent productReservedEvent) {
        //Process user payment
        LOGGER.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());


        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User user = null;

        try {
            user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            // start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            // it triggers Product aggregate to roll back
            return;
        }

        if (user == null) {
            // start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details.");
            // it triggers Product aggregate to roll back
            return;
        }
        LOGGER.info("Successfully fetched user payment details for user " + user.getFirstName());

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            // it triggers Payment Aggregate

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            // start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            // it triggers Product aggregate to roll back
            return;
        }

        if (result == null) {
            LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiatin a compensating transaction");
            // start compensating transaction
            cancelProductReservation(productReservedEvent,
                    "Could not process user payment with provided payment details");
            // it triggers Product aggregate to roll back
        }

    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        CancelProductReservationCommand cancelProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .productId(productReservedEvent.getProductId())
                        .quantity(productReservedEvent.getQuantity())
                        .userId(productReservedEvent.getUserId())
                        .reason(reason)
                        .build();
        commandGateway.send(cancelProductReservationCommand); // it triggers Product aggregate to roll back
    }

    @SagaEventHandler(associationProperty = "orderId") // it is triggered by Payment Aggregate class
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        //send an ApprovedOrderCommand
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand); // it triggers Order Aggregate to update order status

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) { // it is triggered by Order Aggregate to end saga

        LOGGER.info("Order is approved. Order Saga is complete for orderId : " + orderApprovedEvent.getOrderId());
        //SagaLifecycle.end();

    }

    @SagaEventHandler(associationProperty = "orderId") // it is triggered by Product Aggregate class
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        //Create and send RejectOrderCommand

        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                productReservationCancelledEvent.getOrderId(),
                productReservationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand); // it triggers Order aggregate

        LOGGER.info("Product is rejected. Created RejectOrderCommand");

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId") // it is triggered by Order Aggregate
    public void handle(OrderRejectedEvent orderRejectedEvent) {

        LOGGER.info("Order is successfully rejected with orderId : " + orderRejectedEvent.getOrderId());

    }
}
