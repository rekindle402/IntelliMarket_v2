package com.intellimarket.intellimarket.domain.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerApplyRequest {

    @NotBlank(message = "상호명은 필수입니다.")
    @Size(max = 50, message = "상호명은 50자 이내로 입력해주세요")
    private String businessName;

    @NotBlank(message = "사업자 등록 번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자 등록 번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
    private String businessRegistrationNo;

    @NotBlank(message = "대표자명은 필수입니다.")
    @Size(max = 30, message = "대표자명은 30자 이내로 입력해주세요")
    private String representativeName;
}
