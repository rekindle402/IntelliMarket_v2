package com.intellimarket.intellimarket.common.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COM001", "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
