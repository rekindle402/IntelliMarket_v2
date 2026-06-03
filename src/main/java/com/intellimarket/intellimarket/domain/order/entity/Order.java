package com.intellimarket.intellimarket.domain.order.entity;

import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.entity.MemberAddress;
import com.intellimarket.intellimarket.domain.order.enums.OrderStatus;
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
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_address_id")
    private MemberAddress memberAddress;

    @Column(name = "order_no", nullable = false, unique = true, length = 100)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus;

    @Column(name = "total_product_amount", nullable = false)
    private Integer totalProductAmount;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    @Column(name = "final_payment_amount", nullable = false)
    private Integer finalPaymentAmount;

    @Column(name = "store_name_snapshot", nullable = false, length = 150)
    private String storeNameSnapshot;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Builder
    public Order(Member member, Store store, MemberAddress memberAddress, String orderNo,
                 OrderStatus orderStatus, Integer totalProductAmount, Integer deliveryFee,
                 Integer finalPaymentAmount, String storeNameSnapshot, LocalDateTime orderedAt) {
        this.member = member;
        this.store = store;
        this.memberAddress = memberAddress;
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.totalProductAmount = totalProductAmount;
        this.deliveryFee = deliveryFee;
        this.finalPaymentAmount = finalPaymentAmount;
        this.storeNameSnapshot = storeNameSnapshot;
        this.orderedAt = orderedAt;
    }

    public void markAsPaid(LocalDateTime paidAt) {
        this.orderStatus = OrderStatus.PAID;
        this.paidAt = paidAt;
    }

    public void cancel(LocalDateTime cancelledAt) {
        this.orderStatus = OrderStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
