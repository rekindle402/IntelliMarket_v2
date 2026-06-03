package com.intellimarket.intellimarket.domain.order.entity;

import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_shipping_snapshots")
public class OrderShippingSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "recipient_name_snapshot", nullable = false, length = 100)
    private String recipientNameSnapshot;

    @Column(name = "phone_number_snapshot", nullable = false, length = 30)
    private String phoneNumberSnapshot;

    @Column(name = "postal_code_snapshot", nullable = false, length = 20)
    private String postalCodeSnapshot;

    @Column(name = "address_line1_snapshot", nullable = false)
    private String addressLine1Snapshot;

    @Column(name = "address_line2_snapshot")
    private String addressLine2Snapshot;

    @Column(name = "delivery_memo_snapshot")
    private String deliveryMemoSnapshot;

    @Builder
    public OrderShippingSnapshot(Order order, String recipientNameSnapshot, String phoneNumberSnapshot,
                                 String postalCodeSnapshot, String addressLine1Snapshot,
                                 String addressLine2Snapshot, String deliveryMemoSnapshot) {
        this.order = order;
        this.recipientNameSnapshot = recipientNameSnapshot;
        this.phoneNumberSnapshot = phoneNumberSnapshot;
        this.postalCodeSnapshot = postalCodeSnapshot;
        this.addressLine1Snapshot = addressLine1Snapshot;
        this.addressLine2Snapshot = addressLine2Snapshot;
        this.deliveryMemoSnapshot = deliveryMemoSnapshot;
    }
}
