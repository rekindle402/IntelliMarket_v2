package com.intellimarket.intellimarket.domain.member.dto;

import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.enums.Gender;
import com.intellimarket.intellimarket.domain.member.enums.MemberRole;
import com.intellimarket.intellimarket.domain.member.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberProfileResponse {
    private Long memberId;
    private String email;
    private String name;
    private Integer birthYear;
    private Gender gender;
    private MemberRole role;
    private MemberStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    public static MemberProfileResponse from(Member member){
        return MemberProfileResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .birthYear(member.getBirthYear())
                .gender(member.getGender())
                .role(member.getMemberRole())
                .status(member.getMemberStatus())
                .lastLoginAt(member.getLastLoginAt())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
