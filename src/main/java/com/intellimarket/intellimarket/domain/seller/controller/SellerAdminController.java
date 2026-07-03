package com.intellimarket.intellimarket.domain.seller.controller;

import com.intellimarket.intellimarket.common.ApiResponse;
import com.intellimarket.intellimarket.common.PageResponse;
import com.intellimarket.intellimarket.domain.seller.dto.SellerAdminResponse;
import com.intellimarket.intellimarket.domain.seller.dto.SellerRejectRequest;
import com.intellimarket.intellimarket.domain.seller.dto.SellerSearchCondition;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import com.intellimarket.intellimarket.domain.seller.service.SellerAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sellers")
public class SellerAdminController {
    private final SellerAdminService sellerAdminService;


    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SellerAdminResponse>>> applyList(@RequestParam(required = false) SellerStatus status, Pageable pageable){
        SellerSearchCondition condition = new SellerSearchCondition(status);
        PageResponse<SellerAdminResponse> response = new PageResponse<>(sellerAdminService.getSellerList(condition, pageable));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<ApiResponse<SellerAdminResponse>> getDetail(@PathVariable Long sellerId) {
        SellerAdminResponse response = sellerAdminService.getSellerDetail(sellerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{sellerId}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long sellerId) {
        sellerAdminService.approveSeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{sellerId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long sellerId, @Valid @RequestBody SellerRejectRequest request) {
        sellerAdminService.rejectSeller(sellerId, request.getRejectionReason());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
