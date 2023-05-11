package com.example.productservice.query;

import com.example.core.commands.events.ProductReservedEvent;
import com.example.productservice.core.data.ProductEntity;
import com.example.productservice.core.data.ProductRepository;
import com.example.productservice.core.events.ProductCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private final ProductRepository productsRepository;

    @ExceptionHandler(resultType = Exception.class)
    public void handleException(Exception ex) throws Exception{
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
    public void on(ProductReservedEvent event){
       ProductEntity entity = productsRepository.findByProductId(event.getProductId());
       entity.setQuantity(entity.getQuantity() -  event.getQuantity());
       productsRepository.save(entity);
    }
}
