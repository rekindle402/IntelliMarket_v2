package com.intellimarket.intellimarket.domain.member.dto;

import com.intellimarket.intellimarket.domain.member.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpdateRequest {
    @NotBlank(message = "닉네임은 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$",
            message = "닉네임은 2~20자 한글, 영문, 숫자만 가능합니다")
    private String name;

    @Min(value = 1900, message = "올바른 출생연도가 아닙니다")
    @Max(value = 2024, message = "올바른 출생연도가 아닙니다")
    private Integer birthYear;

    private Gender gender;
}
