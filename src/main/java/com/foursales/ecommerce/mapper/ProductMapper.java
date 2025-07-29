package com.foursales.ecommerce.mapper;

import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.resource.request.CreateProductRequest;
import com.foursales.ecommerce.resource.response.ProductResponse;

public class ProductMapper {

    public static Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
