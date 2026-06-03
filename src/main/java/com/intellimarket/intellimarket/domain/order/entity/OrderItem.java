package com.intellimarket.intellimarket.domain.order.entity;

import com.intellimarket.intellimarket.domain.order.enums.OrderItemStatus;
import com.intellimarket.intellimarket.domain.product.entity.Product;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name_snapshot", nullable = false, length = 200)
    private String productNameSnapshot;

    @Column(name = "product_price_snapshot", nullable = false)
    private Integer productPriceSnapshot;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_amount", nullable = false)
    private Integer itemAmount;

    @Column(name = "product_option_snapshot")
    private String productOptionSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false, length = 30)
    private OrderItemStatus orderItemStatus;

    @Builder
    public OrderItem(Order order, Product product, String productNameSnapshot,
                     Integer productPriceSnapshot, Integer quantity, Integer itemAmount,
                     String productOptionSnapshot, OrderItemStatus orderItemStatus) {
        this.order = order;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.productPriceSnapshot = productPriceSnapshot;
        this.quantity = quantity;
        this.itemAmount = itemAmount;
        this.productOptionSnapshot = productOptionSnapshot;
        this.orderItemStatus = orderItemStatus;
    }

    public void cancel() {
        this.orderItemStatus = OrderItemStatus.CANCELLED;
    }

    public void refund() {
        this.orderItemStatus = OrderItemStatus.REFUNDED;
    }
}
