package com.intellimarket.intellimarket.domain.member.dto;

import com.intellimarket.intellimarket.domain.member.enums.MemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberStatusUpdateRequest {
    @NotNull(message = "변경할 상태는 필수입니다")
    private MemberStatus status;
}
