package com.example.productservice.controller;

import com.example.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String welcome(){
        return productService.welcome();
    }
}
