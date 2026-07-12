package com.intellimarket.intellimarket.domain.seller.service;

import com.intellimarket.intellimarket.common.errorcode.SellerErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SellerAdminServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private com.intellimarket.intellimarket.domain.seller.repository.SellerQueryRepository sellerQueryRepository;

    @InjectMocks
    private SellerAdminService sellerAdminService;

    @Test
    @DisplayName("PENDING 상태의 판매자를 승인하면 상태가 APPROVED로 변경된다")
    void approveSeller_success() {
        // Arrange
        Seller seller = mock(Seller.class);
        given(sellerRepository.findById(1L)).willReturn(Optional.of(seller));
        given(seller.getSellerStatus()).willReturn(SellerStatus.PENDING);

        // Act
        sellerAdminService.approveSeller(1L);

        // Assert
        org.mockito.Mockito.verify(seller).approve();
    }

    @Test
    @DisplayName("이미 승인된 판매자를 다시 승인하려 하면 예외가 발생한다")
    void approveSeller_fail_alreadyProcessed() {
        // Arrange
        Seller seller = mock(Seller.class);
        given(sellerRepository.findById(1L)).willReturn(Optional.of(seller));
        given(seller.getSellerStatus()).willReturn(SellerStatus.APPROVED);

        // Act & Assert
        assertThatThrownBy(() -> sellerAdminService.approveSeller(1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(SellerErrorCode.SELLER_ALREADY_PROCESSED));
    }

    @Test
    @DisplayName("PENDING 상태의 판매자를 거절하면 상태가 REJECTED로 변경된다")
    void rejectSeller_success() {
        // Arrange
        Seller seller = mock(Seller.class);
        given(sellerRepository.findById(1L)).willReturn(Optional.of(seller));
        given(seller.getSellerStatus()).willReturn(SellerStatus.PENDING);

        // Act
        sellerAdminService.rejectSeller(1L, "서류 미비");

        // Assert
        org.mockito.Mockito.verify(seller).reject("서류 미비");
    }

    @Test
    @DisplayName("존재하지 않는 판매자를 조회하면 예외가 발생한다")
    void getSellerDetail_fail_notFound() {
        // Arrange
        given(sellerRepository.findById(999L)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sellerAdminService.getSellerDetail(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(SellerErrorCode.SELLER_NOT_FOUND));
    }
}
