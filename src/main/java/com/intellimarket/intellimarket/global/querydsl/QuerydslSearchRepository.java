package com.intellimarket.intellimarket.global.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

public interface QuerydslSearchRepository<T, C> {

    Page<T> findAll(C condition, Pageable pageable);

    default <R> Page<R> toPage(JPAQuery<R> contentQuery, JPAQuery<Long> countQuery, Pageable pageable) {
        List<R> content = contentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    default BooleanExpression[] toArray(BooleanExpression... expressions) {
        return Arrays.stream(expressions)
                .filter(e -> e != null)
                .toArray(BooleanExpression[]::new);
    }
}
