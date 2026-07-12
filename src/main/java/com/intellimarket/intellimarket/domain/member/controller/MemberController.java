package com.intellimarket.intellimarket.domain.member.controller;

import com.intellimarket.intellimarket.common.ApiResponse;
import com.intellimarket.intellimarket.domain.auth.security.CustomUserDetails;
import com.intellimarket.intellimarket.domain.member.dto.MemberProfileResponse;
import com.intellimarket.intellimarket.domain.member.dto.MemberUpdateRequest;
import com.intellimarket.intellimarket.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberProfileResponse response = memberService.getMyProfile(userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                @Valid @RequestBody MemberUpdateRequest request) {
        MemberProfileResponse response = memberService.updateMyProfile(userDetails.getMember().getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.withdraw(userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
