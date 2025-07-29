package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.Product;
import com.foursales.ecommerce.enums.ProductStatus;
import com.foursales.ecommerce.mapper.ProductMapper;
import com.foursales.ecommerce.repository.ProductRepository;
import com.foursales.ecommerce.resource.request.CreateProductRequest;
import com.foursales.ecommerce.resource.request.UpdateProductRequest;
import com.foursales.ecommerce.resource.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final UUID productId = UUID.randomUUID();

    private CreateProductRequest createProductRequest;
    private UpdateProductRequest updateRequest;

    @Nested
    class Create {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProduct() {
            buildCreateProductRequest();

            Product product = ProductMapper.toEntity(createProductRequest);

            when(productRepository.save(any(Product.class))).thenReturn(product);

            ProductResponse response = productService.create(createProductRequest);

            assertEquals(createProductRequest.name(), response.getName());
            assertEquals(createProductRequest.description(), response.getDescription());
            assertEquals(createProductRequest.price(), response.getPrice());
            assertEquals(createProductRequest.category(), response.getCategory());
            assertEquals(createProductRequest.stockQuantity(), response.getStockQuantity());

            verify(productRepository).save(any(Product.class));
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    class Update {

        @Test
        @DisplayName("Should update product successfully when product exists")
        void shouldUpdateProduct() {
            buildUpdatedProductRequest();

            Product product = new Product();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            ProductResponse response = productService.update(productId, updateRequest);

            assertEquals(updateRequest.name(), response.getName());
            assertEquals(updateRequest.description(), response.getDescription());
            assertEquals(updateRequest.price(), response.getPrice());
            assertEquals(updateRequest.category(), response.getCategory());
            assertEquals(updateRequest.stockQuantity(), response.getStockQuantity());

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product does not exist")
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> productService.update(productId, updateRequest));

            assertEquals("Product not found: " + productId, exception.getMessage());

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    class Remove {

        @Test
        @DisplayName("Should set product status to INACTIVE when product exists")
        void shouldRemoveProduct() {
            Product product = new Product();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            productService.remove(productId);

            assertEquals(product.getStatus(), ProductStatus.INACTIVE);

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product to remove does not exist")
        void shouldThrowWhenRemoveProductNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> productService.remove(productId));
            assertEquals("Product not found: " + productId, exception.getMessage());

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    class FindAll {

        @Test
        @DisplayName("Should return paged product responses")
        void shouldReturnPagedProducts() {
            Pageable pageable = PageRequest.of(0, 10);

            Product product = buildProduct();

            Page<Product> productPage = new PageImpl<>(List.of(product));
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            Page<ProductResponse> result = productService.findAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            ProductResponse response = result.getContent().get(0);
            assertEquals(product.getName(), response.getName());
            assertEquals(product.getDescription(), response.getDescription());
            assertEquals(product.getPrice(), response.getPrice());
            assertEquals(product.getCategory(), response.getCategory());
            assertEquals(product.getStockQuantity(), response.getStockQuantity());

            verify(productRepository).findAll(pageable);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    class FindById {

        @Test
        @DisplayName("Should return product response when product found")
        void shouldReturnProductResponse() {
            Product product = buildProduct();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            ProductResponse response = productService.findById(productId);

            assertEquals(product.getName(), response.getName());
            assertEquals(product.getDescription(), response.getDescription());
            assertEquals(product.getPrice(), response.getPrice());
            assertEquals(product.getCategory(), response.getCategory());
            assertEquals(product.getStockQuantity(), response.getStockQuantity());

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product not found")
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> productService.findById(productId));

            assertEquals("Product not found: " + productId, exception.getMessage());

            verify(productRepository).findById(productId);
            verifyNoMoreInteractions(productRepository);
        }
    }

    private void buildCreateProductRequest() {
        createProductRequest = new CreateProductRequest(
                "Laptop",
                "High performance laptop",
                new BigDecimal("3999.90"),
                "Electronics",
                10
        );
    }

    private void buildUpdatedProductRequest() {
        updateRequest = new UpdateProductRequest(
                "Updated Laptop",
                "Updated description",
                new BigDecimal("4999.99"),
                "Updated Category",
                5
        );
    }

    private static Product buildProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("3999.99"))
                .category("Electronics")
                .stockQuantity(10)
                .build();
    }
}
