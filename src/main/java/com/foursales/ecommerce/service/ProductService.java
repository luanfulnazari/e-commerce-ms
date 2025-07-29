package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.enums.ProductStatus;
import com.foursales.ecommerce.mapper.ProductMapper;
import com.foursales.ecommerce.repository.ProductRepository;
import com.foursales.ecommerce.resource.request.CreateProductRequest;
import com.foursales.ecommerce.resource.request.UpdateProductRequest;
import com.foursales.ecommerce.resource.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = ProductMapper.toEntity(request);
        productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        return productRepository.findById(id).map(product -> {
            product.updateFrom(request);
            return ProductMapper.toResponse(product);
        }).orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    @Transactional
    public void remove(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        product.setStatus(ProductStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return productRepository.findById(id).map(ProductMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }
}
