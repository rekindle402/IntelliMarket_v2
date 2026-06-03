package com.intellimarket.intellimarket.domain.category.entity;

import com.intellimarket.intellimarket.domain.category.enums.CategoryStatus;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "category_code", nullable = false, unique = true, length = 100)
    private String categoryCode;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(nullable = false)
    private Integer depth;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_status", nullable = false, length = 30)
    private CategoryStatus categoryStatus;

    @Builder
    public Category(Category parent, String categoryCode, String categoryName,
                    Integer depth, Integer sortOrder, CategoryStatus categoryStatus) {
        this.parent = parent;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.depth = depth;
        this.sortOrder = sortOrder;
        this.categoryStatus = categoryStatus;
    }

    public void deactivate() {
        this.categoryStatus = CategoryStatus.INACTIVE;
    }

    public void activate() {
        this.categoryStatus = CategoryStatus.ACTIVE;
    }
}
