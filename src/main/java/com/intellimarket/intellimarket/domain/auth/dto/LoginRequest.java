package com.intellimarket.intellimarket.domain.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
