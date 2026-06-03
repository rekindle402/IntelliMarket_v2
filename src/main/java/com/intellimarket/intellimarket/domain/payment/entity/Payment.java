package com.intellimarket.intellimarket.domain.payment.entity;

import com.intellimarket.intellimarket.domain.order.entity.Order;
import com.intellimarket.intellimarket.domain.payment.enums.PaymentStatus;
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
@Table(name = "payments")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_no", nullable = false, unique = true, length = 100)
    private String paymentNo;

    @Column(name = "payment_provider", nullable = false, length = 50)
    private String paymentProvider;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Column(name = "provider_payment_key", unique = true)
    private String providerPaymentKey;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Builder
    public Payment(Order order, String paymentNo, String paymentProvider, String paymentMethod,
                   PaymentStatus paymentStatus, Integer paymentAmount, LocalDateTime requestedAt) {
        this.order = order;
        this.paymentNo = paymentNo;
        this.paymentProvider = paymentProvider;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentAmount = paymentAmount;
        this.requestedAt = requestedAt;
    }

    public void approve(String providerPaymentKey, LocalDateTime approvedAt) {
        this.paymentStatus = PaymentStatus.APPROVED;
        this.providerPaymentKey = providerPaymentKey;
        this.approvedAt = approvedAt;
    }

    public void fail() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void cancel(LocalDateTime cancelledAt) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
    }

    public void partialCancel(LocalDateTime cancelledAt) {
        this.paymentStatus = PaymentStatus.PARTIAL_CANCELLED;
        this.cancelledAt = cancelledAt;
    }
}
