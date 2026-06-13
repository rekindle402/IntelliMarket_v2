package com.intellimarket.intellimarket.domain.seller.service;

import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.auth.service.AuthService;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final AuthService authService;

    public Seller apply(Long memberId, SellerApplyRequest request) {
        Optional<Member> member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND))

        Seller seller = Seller.create(member, request.getBusinessName(), request.getBusinessRegistrationNo(), request.getRepresentativeName());

        sellerRepository.save();
    }


}
