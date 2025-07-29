package com.foursales.ecommerce.entity;

import com.foursales.ecommerce.enums.ProductStatus;
import com.foursales.ecommerce.resource.request.UpdateProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
@SQLRestriction("status = 'ACTIVE'")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(UpdateProductRequest request) {
        Optional.ofNullable(request.name()).ifPresent(this::setName);
        Optional.ofNullable(request.description()).ifPresent(this::setDescription);
        Optional.ofNullable(request.price()).ifPresent(this::setPrice);
        Optional.ofNullable(request.category()).ifPresent(this::setCategory);
        Optional.ofNullable(request.stockQuantity()).ifPresent(this::setStockQuantity);
    }
}
