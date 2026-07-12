package com.intellimarket.intellimarket.common.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();

    default String getError(){
        return ((Enum<?>) this).name();
    }
}
