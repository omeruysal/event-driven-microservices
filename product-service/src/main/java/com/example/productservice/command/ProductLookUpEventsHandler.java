package com.example.productservice.command;

import com.example.productservice.core.data.ProductLookUpRepository;
import com.example.productservice.core.data.ProductLookupEntity;
import com.example.productservice.core.events.ProductCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@AllArgsConstructor
// we annotate our handlers to listen same publisher and be managed with same thread,
// if event processing is not successful we can roll back all transaction
public class ProductLookUpEventsHandler {

    private final ProductLookUpRepository productLookUpRepository;

    @EventHandler
    public void on(ProductCreatedEvent event){
        ProductLookupEntity entity = new ProductLookupEntity(event.getProductId(), event.getTitle());
        productLookUpRepository.save(entity);
    }
}
