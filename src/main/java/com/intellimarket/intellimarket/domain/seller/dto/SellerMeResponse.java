package com.intellimarket.intellimarket.domain.seller.dto;

import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerMeResponse {

    private Long sellerId;
    private String businessName;
    private String businessRegistrationNo;
    private String representativeName;
    private SellerStatus sellerStatus;

    public static SellerMeResponse from(Seller seller) {
        return SellerMeResponse.builder()
                .sellerId(seller.getId())
                .businessName(seller.getBusinessName())
                .businessRegistrationNo(seller.getBusinessRegistrationNo())
                .representativeName(seller.getRepresentativeName())
                .sellerStatus(seller.getSellerStatus())
                .build();
    }
}
