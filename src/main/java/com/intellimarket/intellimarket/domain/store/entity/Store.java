package com.intellimarket.intellimarket.domain.store.entity;

import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.store.enums.StoreStatus;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stores")
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private Seller seller;

    @Column(name = "store_name", nullable = false, unique = true, length = 150)
    private String storeName;

    @Column(name = "store_slug", nullable = false, unique = true, length = 150)
    private String storeSlug;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false, length = 30)
    private StoreStatus storeStatus;

    @Builder
    public Store(Seller seller, String storeName, String storeSlug,
                 String description, StoreStatus storeStatus) {
        this.seller = seller;
        this.storeName = storeName;
        this.storeSlug = storeSlug;
        this.description = description;
        this.storeStatus = storeStatus;
    }

    public void suspend() {
        this.storeStatus = StoreStatus.SUSPENDED;
    }

    public void deactivate() {
        this.storeStatus = StoreStatus.INACTIVE;
    }

    public void activate() {
        this.storeStatus = StoreStatus.ACTIVE;
    }

    public void updateInfo(String storeName, String storeSlug, String description) {
        this.storeName = storeName;
        this.storeSlug = storeSlug;
        this.description = description;
    }
}
