package com.intellimarket.intellimarket.domain.store.entity;

import com.intellimarket.intellimarket.domain.category.entity.Category;
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
@Table(name = "store_category_preferences")
public class StoreCategoryPreference extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Builder
    public StoreCategoryPreference(Store store, Category category, Boolean isFavorite) {
        this.store = store;
        this.category = category;
        this.isFavorite = isFavorite;
        this.usageCount = 0;
    }

    public void incrementUsage(LocalDateTime usedAt) {
        this.usageCount++;
        this.lastUsedAt = usedAt;
    }

    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }
}
