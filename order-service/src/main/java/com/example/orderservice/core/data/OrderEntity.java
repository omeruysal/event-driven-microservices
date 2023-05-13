package com.example.orderservice.core.data;

import com.example.orderservice.core.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @Column(unique = true)
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated
    private OrderStatus orderStatus;
}
