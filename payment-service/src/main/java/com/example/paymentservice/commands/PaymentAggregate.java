package com.example.paymentservice.commands;

import com.example.core.commands.ProcessPaymentCommand;
import com.example.core.commands.data.PaymentDetails;
import com.example.core.commands.events.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    public PaymentAggregate(){

    }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand){
        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent();

        BeanUtils.copyProperties(processPaymentCommand, paymentProcessedEvent);

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event){
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }
}
