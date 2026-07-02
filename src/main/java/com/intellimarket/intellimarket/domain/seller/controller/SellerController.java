package com.intellimarket.intellimarket.domain.seller.controller;

import com.intellimarket.intellimarket.common.ApiResponse;
import com.intellimarket.intellimarket.domain.auth.security.CustomUserDetails;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.dto.SellerMeResponse;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {
    private final SellerService sellerService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SellerMeResponse>> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Seller seller = sellerService.getMe(userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(SellerMeResponse.from(seller)));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Void>> apply(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody SellerApplyRequest request) {
        Long id = userDetails.getMember().getId();
        sellerService.apply(id, request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
