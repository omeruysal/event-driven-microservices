package com.example.orderservice.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderDto {
    @NotBlank(message = "Product Id is mandatory")
    private  String productId;
    @Min(value = 1, message = "Quantity can not be less than 1")
    @Max(value = 5, message = "Quantity can not be higher than 5")
    private  Integer quantity;
    @NotBlank(message = "Address Id is mandatory")
    private  String addressId;
}
