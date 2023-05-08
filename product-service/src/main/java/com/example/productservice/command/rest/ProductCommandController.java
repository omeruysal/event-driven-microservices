package com.example.productservice.command.rest;

import com.example.productservice.command.CreateProductCommand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel request) {
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
    public String port() {
        return env.getProperty("local.server.port");
    }
}
