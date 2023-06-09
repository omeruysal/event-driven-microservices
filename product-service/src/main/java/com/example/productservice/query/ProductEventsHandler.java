package com.example.productservice.query;

import com.example.core.commands.events.ProductReservationCancelledEvent;
import com.example.core.commands.events.ProductReservedEvent;
import com.example.productservice.core.data.ProductEntity;
import com.example.productservice.core.data.ProductRepository;
import com.example.productservice.core.events.ProductCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private final ProductRepository productsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);

    @ExceptionHandler(resultType = Exception.class)
    public void handleException(Exception ex) throws Exception {
        throw ex;
    }


    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {

        ProductEntity entity = new ProductEntity();
        BeanUtils.copyProperties(event, entity);

        productsRepository.save(entity);
        //if(true) throw new Exception("Comes from event handler");
        // transaction is not commit yet. It will be roll back even we throw exception after saving entity
    }

    @EventHandler // it will be triggered when ProductReservedEvent is published
    public void on(ProductReservedEvent event) {
        ProductEntity entity = productsRepository.findByProductId(event.getProductId());
        entity.setQuantity(entity.getQuantity() - event.getQuantity());
        productsRepository.save(entity);

        LOGGER.info("ProductReservedEvent is called for productId: " + event.getProductId() +
                " and orderId: " + event.getOrderId());
    }

    @EventHandler // it will be triggered when ProductReservationCancelledEvent is published
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        ProductEntity entity = productsRepository.findByProductId(productReservationCancelledEvent.getProductId());

        int newQuantity = entity.getQuantity() + productReservationCancelledEvent.getQuantity();

        entity.setQuantity(newQuantity);
        productsRepository.save(entity);

        LOGGER.info("ProductReservationCancelledEvent is called for productId: " + productReservationCancelledEvent.getProductId() +
                " and orderId: " + productReservationCancelledEvent.getOrderId());
    }
}
