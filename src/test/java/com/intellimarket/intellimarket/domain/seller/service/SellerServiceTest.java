package com.intellimarket.intellimarket.domain.seller.service;

import com.intellimarket.intellimarket.common.errorcode.SellerErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private SellerApplyRequest request;

    @BeforeEach() // 테스트를 수행할때 매번 먼저 수행됨
    void setUp() {
        request = SellerApplyRequest.builder()
                .businessName("주식회사 테스트")
                .representativeName("김대표")
                .businessRegistrationNo("123-45-67890")
                .build();
    }

    @Test
    @DisplayName("이미 존재하는 사업자등록번호로 가입을 시도하면 BUSINESS_NUMBER_ALREADY_EXISTS 예외가 발생한다")
    void apply_fail_businessNumberAreadyExists(){
        given(sellerRepository.existsByBusinessRegistrationNo(request.getBusinessRegistrationNo())).willReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> sellerService.apply(1L,request)
        );

        assertThat(exception.getErrorCode()).isEqualTo(SellerErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("이미 판매자 가입 신청한 유저가 가입을 시도하면 ALREADY_APPLIED 예외가 발생한다")
    void apply_fail_alreadyApplied(){
        given(sellerRepository.existsByMemberId(1L)).willReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> sellerService.apply(1L,request)
        );

        assertThat(exception.getErrorCode()).isEqualTo(SellerErrorCode.ALREADY_APPLIED);
    }


}
