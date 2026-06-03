package com.intellimarket.intellimarket.domain.product.entity;

import com.intellimarket.intellimarket.domain.category.entity.Category;
import com.intellimarket.intellimarket.domain.product.enums.ProductStatus;
import com.intellimarket.intellimarket.domain.store.entity.Store;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 30)
    private ProductStatus productStatus;

    @Column(name = "displayed_at")
    private LocalDateTime displayedAt;

    @Builder
    public Product(Store store, Category category, String productName, String description,
                   Integer price, Integer stockQuantity, ProductStatus productStatus,
                   LocalDateTime displayedAt) {
        this.store = store;
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.productStatus = productStatus;
        this.displayedAt = displayedAt;
    }

    public void updateInfo(String productName, String description, Integer price, Category category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public void changeStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public void decreaseStock(int quantity) {
        this.stockQuantity -= quantity;
        if (this.stockQuantity <= 0) {
            this.stockQuantity = 0;
            this.productStatus = ProductStatus.SOLD_OUT;
        }
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
