package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.resource.request.CreateProductRequest;
import com.foursales.ecommerce.resource.request.UpdateProductRequest;
import com.foursales.ecommerce.resource.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductResource.class)
class ProductResourceMvcTest extends AbstractResourceMvcTest {

    private final UUID productId = UUID.randomUUID();
    private final String name = "name";
    private final String description = "description";
    private final BigDecimal price = new BigDecimal("100.00");
    private final String category = "category";
    private final Integer stockQuantity = 5;

    @Nested
    class CreateProduct {

        @Test
        @DisplayName("Should return 201 and created product response")
        void shouldCreateProduct() throws Exception {
            CreateProductRequest request = new CreateProductRequest(name, description, price, category, stockQuantity);

            ProductResponse response = new ProductResponse(productId, name, description, price, category, stockQuantity);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(productService.create(any(CreateProductRequest.class))).thenReturn(response);

            mockMvc.perform(post("/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(expectedJson));

            verify(productService).create(any(CreateProductRequest.class));
        }

        @Test
        @DisplayName("Should return BadRequest when required fields are blank")
        void shouldReturnBadRequest_whenRequiredFieldsAreBlank() throws Exception {
            CreateProductRequest request = new CreateProductRequest("", "", price, "", stockQuantity);

            mockMvc.perform(post("/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message.size()")
                            .value(3))
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be blank"));

            verify(productService, never()).create(any());
        }

        @Test
        @DisplayName("Should return BadRequest when required fields are null")
        void shouldReturnBadRequest_whenRequiredFieldsAreNull() throws Exception {
            CreateProductRequest request = new CreateProductRequest(name, description, null, category, null);

            mockMvc.perform(post("/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message.size()")
                            .value(2))
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be null"));

            verify(productService, never()).create(any());
        }
    }

    @Nested
    class UpdateProduct {

        @Test
        @DisplayName("Should return 200 and updated product response")
        void shouldUpdateProduct() throws Exception {
            UpdateProductRequest request = new UpdateProductRequest(name, description, price, category, stockQuantity);

            ProductResponse response = new ProductResponse(productId, name, description, price, category, stockQuantity);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(productService.update(eq(productId), any(UpdateProductRequest.class))).thenReturn(response);

            mockMvc.perform(put("/v1/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(productService).update(eq(productId), any(UpdateProductRequest.class));
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        @DisplayName("Should return 204 on successful delete")
        void shouldDeleteProduct() throws Exception {
            doNothing().when(productService).remove(productId);

            mockMvc.perform(delete("/v1/products/{id}", productId))
                    .andExpect(status().isNoContent());

            verify(productService).remove(productId);
        }
    }

    @Nested
    class FindAllProducts {

        @Test
        @DisplayName("Should return 200 and paged product list")
        void shouldReturnPagedProducts() throws Exception {
            ProductResponse response = new ProductResponse(productId, name, description, price, category, stockQuantity);
            Page<ProductResponse> page = new PageImpl<>(List.of(response));
            String expectedJson = objectMapper.writeValueAsString(page);
            when(productService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/v1/products")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sort", "name,asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(productService).findAll(any(Pageable.class));
        }
    }

    @Nested
    class FindProductById {

        @Test
        @DisplayName("Should return 200 and product by id")
        void shouldReturnProductById() throws Exception {
            ProductResponse response = new ProductResponse(productId, name, description, price, category, stockQuantity);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(productService.findById(productId)).thenReturn(response);

            mockMvc.perform(get("/v1/products/{id}", productId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(productService).findById(productId);
        }
    }
}
