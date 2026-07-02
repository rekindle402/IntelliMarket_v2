package com.intellimarket.intellimarket.common.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SellerErrorCode implements ErrorCode{

    BUSINESS_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "SEL001", "이미 가입된 사업자등록번호입니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "SEL002", "이미 판매자 신청 내역이 있습니다."),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "SEL003", "판매자 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
