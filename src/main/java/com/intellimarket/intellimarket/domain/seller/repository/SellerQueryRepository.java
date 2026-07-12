package com.intellimarket.intellimarket.domain.seller.repository;

import com.intellimarket.intellimarket.domain.seller.dto.SellerSearchCondition;
import com.intellimarket.intellimarket.domain.seller.entity.QSeller;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import com.intellimarket.intellimarket.global.querydsl.QuerydslSearchRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SellerQueryRepository implements QuerydslSearchRepository<Seller, SellerSearchCondition> {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Seller> findAll(SellerSearchCondition condition, Pageable pageable) {
        QSeller seller = QSeller.seller;

        JPAQuery<Seller> contentQuery = queryFactory
                .selectFrom(seller)
                .where(toArray(
                        statusEq(condition.status(), seller)
                ));

        JPAQuery<Long> countQuery = queryFactory
                .select(seller.count())
                .from(seller)
                .where(toArray(
                        statusEq(condition.status(),seller)
                ));

        return toPage(contentQuery, countQuery, pageable);
    }

    private BooleanExpression statusEq(SellerStatus status, QSeller seller) {
        return status != null ? seller.sellerStatus.eq(status) : null;
    }
}
