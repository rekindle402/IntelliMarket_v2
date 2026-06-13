package com.intellimarket.intellimarket.domain.member.controller;

import com.intellimarket.intellimarket.common.ApiResponse;
import com.intellimarket.intellimarket.common.PageResponse;
import com.intellimarket.intellimarket.domain.member.dto.MemberProfileResponse;
import com.intellimarket.intellimarket.domain.member.dto.MemberStatusUpdateRequest;
import com.intellimarket.intellimarket.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminMemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MemberProfileResponse>>> getMembers(Pageable pageable) {
        PageResponse<MemberProfileResponse> response = new PageResponse<>(memberService.getMembers(pageable));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMemberDetail(@PathVariable Long memberId) {
        MemberProfileResponse response = memberService.getMemberDetail(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<ApiResponse<Void>> changeMemberStatus(@PathVariable Long memberId,
                                                                  @Valid @RequestBody MemberStatusUpdateRequest request) {
        memberService.changeMemberStatus(memberId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
