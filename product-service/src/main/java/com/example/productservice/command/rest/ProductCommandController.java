package com.example.productservice.controller;

import com.example.productservice.command.CreateProductCommand;
import com.example.productservice.service.CreateProductRestModel;
import com.example.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/hello")
public class ProductCommandController {

    private final Environment env;
    private final ProductService productService;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel request) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .title(request.getTitle())
                .productId(UUID.randomUUID().toString()).build();
        String result = "";

        try {
            result = commandGateway.sendAndWait(createProductCommand);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return result;
    }

    @GetMapping
    public String welcome() {
        return productService.welcome() + env.getProperty("local.server.port");
    }
}
