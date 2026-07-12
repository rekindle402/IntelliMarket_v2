package com.intellimarket.intellimarket.domain.seller.dto;

import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SellerAdminResponse {
    private Long sellerId;
    private String businessName;
    private String representativeName;
    private String businessRegistrationNo;
    private SellerStatus sellerStatus;
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    public static SellerAdminResponse from(Seller seller){
        return SellerAdminResponse.builder()
                .sellerId(seller.getId())
                .businessName(seller.getBusinessName())
                .representativeName(seller.getRepresentativeName())
                .businessRegistrationNo(seller.getBusinessRegistrationNo())
                .sellerStatus(seller.getSellerStatus())
                .createdAt(seller.getCreatedAt())
                .approvedAt(seller.getApprovedAt())
                .rejectedAt(seller.getRejectedAt())
                .rejectionReason(seller.getRejectionReason())
                .build();
    }
}
