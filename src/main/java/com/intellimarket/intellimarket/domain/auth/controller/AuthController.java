package com.intellimarket.intellimarket.domain.auth.controller;

import com.intellimarket.intellimarket.common.ApiResponse;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    // 회원가입 요청
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
