package com.example.orderservice.query;

import com.example.orderservice.core.data.OrderEntity;
import com.example.orderservice.core.data.OrderEntityRepository;
import com.example.orderservice.core.events.OrderApprovedEvent;
import com.example.orderservice.core.events.OrderCreatedEvent;
import com.example.orderservice.core.events.OrderRejectedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ProcessingGroup("order-group")
public class OrderEventsHandler {

    private final OrderEntityRepository orderEntityRepository;

    @ExceptionHandler(resultType = Exception.class)
    public void handleException(Exception ex) throws Exception {
        throw ex;
    }


    @EventHandler
    public void on(OrderCreatedEvent event) throws Exception {

        OrderEntity entity = new OrderEntity();
        BeanUtils.copyProperties(event, entity);

        orderEntityRepository.save(entity);
        //if(true) throw new Exception("Comes from event handler");
        // transaction is not commit yet. It will be roll back even we throw exception after saving entity
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {

        OrderEntity entity = orderEntityRepository.findByOrderId(event.getOrderId());
        if (entity == null) {
            //todo
            return;
        }
        entity.setOrderStatus(event.getOrderStatus());

        orderEntityRepository.save(entity);
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {

        OrderEntity entity = orderEntityRepository.findByOrderId(event.getOrderId());
        entity.setOrderStatus(event.getOrderStatus());

        orderEntityRepository.save(entity);
    }
}
