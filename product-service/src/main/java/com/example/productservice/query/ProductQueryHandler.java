package com.example.productservice.query;

import com.example.productservice.core.data.ProductEntity;
import com.example.productservice.core.data.ProductRepository;
import com.example.productservice.query.rest.ProductRestModel;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ProductQueryHandler {

    private final ProductRepository productRepository;

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductQuery query) {

        List<ProductRestModel> productRest = new ArrayList<>();
        List<ProductEntity> storedProducts = productRepository.findAll();

        for (ProductEntity entity : storedProducts) {
            ProductRestModel restModel = new ProductRestModel();
            BeanUtils.copyProperties(entity, restModel);
            productRest.add(restModel);
        }

        return productRest;
    }
}
