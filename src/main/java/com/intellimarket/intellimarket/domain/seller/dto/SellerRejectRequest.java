package com.intellimarket.intellimarket.domain.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SellerRejectRequest {

    @NotBlank
    private String rejectionReason;
}
