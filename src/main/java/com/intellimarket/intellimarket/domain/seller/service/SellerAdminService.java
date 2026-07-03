package com.intellimarket.intellimarket.domain.seller.service;

import com.intellimarket.intellimarket.common.errorcode.SellerErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.seller.dto.SellerAdminResponse;
import com.intellimarket.intellimarket.domain.seller.dto.SellerSearchCondition;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import com.intellimarket.intellimarket.domain.seller.repository.SellerQueryRepository;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerAdminService {
    private final SellerRepository sellerRepository;
    private final SellerQueryRepository sellerQueryRepository;

    @Transactional(readOnly = true)
    public Page<SellerAdminResponse> getSellerList(SellerSearchCondition condition, Pageable pageable){
        return sellerQueryRepository.findAll(condition, pageable)
                .map(SellerAdminResponse::from);
    }

    @Transactional(readOnly = true)
    public SellerAdminResponse getSellerDetail(Long sellerId){
        return sellerRepository.findById(sellerId)
                .map(SellerAdminResponse::from)
                .orElseThrow(() -> new BusinessException(SellerErrorCode.SELLER_NOT_FOUND));
    }

    @Transactional
    public void approveSeller(Long sellerId){
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(SellerErrorCode.SELLER_NOT_FOUND));

        SellerStatus status = seller.getSellerStatus();
        if(status != SellerStatus.PENDING && status != SellerStatus.SUSPENDED){
            throw new BusinessException(SellerErrorCode.SELLER_ALREADY_PROCESSED);
        }
        seller.approve();
    }

    @Transactional
    public void rejectSeller(Long sellerId, String rejectedReason){
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(SellerErrorCode.SELLER_NOT_FOUND));

        SellerStatus status = seller.getSellerStatus();
        if(status != SellerStatus.PENDING && status != SellerStatus.SUSPENDED){
            throw new BusinessException(SellerErrorCode.SELLER_ALREADY_PROCESSED);
        }
        seller.reject(rejectedReason);
    }

}
