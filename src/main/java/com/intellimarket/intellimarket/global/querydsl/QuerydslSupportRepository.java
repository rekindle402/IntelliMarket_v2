package com.intellimarket.intellimarket.global.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class QuerydslSupportRepository {

    // 조건 목록에서 null을 제거하고 AND로 조합
    protected BooleanExpression[] toArray(BooleanExpression... expressions) {
        return java.util.Arrays.stream(expressions)
                .filter(e -> e != null)
                .toArray(BooleanExpression[]::new);
    }

    // 공통 페이징 처리
    protected <T> Page<T> toPage(JPAQuery<T> contentQuery, JPAQuery<Long> countQuery, Pageable pageable) {
        List<T> content = contentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
