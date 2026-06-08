package com.intellimarket.intellimarket.domain.auth.dto;

import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.enums.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSessionResponse {
    private Long memberId;
    private String name;
    private MemberRole role;

    public static MemberSessionResponse from(Member member){
        return MemberSessionResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .role(member.getMemberRole())
                .build();
    }
}
