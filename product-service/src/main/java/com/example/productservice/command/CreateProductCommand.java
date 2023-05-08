package com.example.productservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Builder
@Data
public class CreateProductCommand {

    @TargetAggregateIdentifier // Axon framework will use this identifier to associate this command with and aggregate obj
    private final String productId;
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
