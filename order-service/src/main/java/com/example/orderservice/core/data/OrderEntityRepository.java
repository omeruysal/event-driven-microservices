package com.example.orderservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, String> {

    OrderEntity findByOrderId(String orderId);
}
