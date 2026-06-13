package com.intellimarket.intellimarket.domain.seller.service;

import com.intellimarket.intellimarket.common.errorcode.MemberErrorCode;
import com.intellimarket.intellimarket.common.errorcode.SellerErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;

    public Seller apply(Long memberId, SellerApplyRequest request) {
        if(checkBusinessNumberDuplicate(request.getBusinessRegistrationNo())){
            throw new BusinessException(SellerErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        if(sellerRepository.existsByMemberId(memberId)){
            throw new BusinessException(SellerErrorCode.ALREADY_APPLIED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Seller seller = Seller.create(member, request.getBusinessName(), request.getBusinessRegistrationNo(), request.getRepresentativeName());
        return sellerRepository.save(seller);
    }

    private boolean checkBusinessNumberDuplicate(String businessNumber){
        return sellerRepository.existsByBusinessRegistrationNo(businessNumber);
    }
}
