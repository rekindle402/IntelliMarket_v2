package com.intellimarket.intellimarket.domain.seller.repository;

import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByMemberId(Long memberId);
    Optional<Seller> findByMemberId(Long memberId);

    boolean existsByBusinessRegistrationNo(String businessNumber);
}
