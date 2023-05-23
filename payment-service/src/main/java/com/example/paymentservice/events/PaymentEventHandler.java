package com.example.paymentservice.events;

import com.example.core.commands.events.PaymentProcessedEvent;
import com.example.paymentservice.data.PaymentEntity;
import com.example.paymentservice.data.PaymentEntityRepository;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentEventHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventHandler.class);

    private final PaymentEntityRepository paymentEntityRepository;

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        LOGGER.info("PaymentProcessedEvent is called for orderId : " + event.getOrderId());
        PaymentEntity entity = new PaymentEntity();

        BeanUtils.copyProperties(event, entity);

        paymentEntityRepository.save(entity);
    }
}
