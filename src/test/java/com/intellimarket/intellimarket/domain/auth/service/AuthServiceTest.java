package com.intellimarket.intellimarket.domain.auth.service;

import com.intellimarket.intellimarket.common.errorcode.AuthErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("이미 존재하는 이메일로 가입을 시도하면 EMAIL_ALREADY_EXISTS 예외가 발생한다")
    void signup_fail_emailAlreadyExists() {
        // Arrange: 이메일이 이미 존재한다고 가정
        SignupRequest request = SignupRequest.builder()
                .email("test@test.com")
                .password("testPassword1@")
                .build();

        given(memberRepository.existsByEmail(request.getEmail())).willReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.signup(request)
        );

        // Act & Assert: signup 호출 시 BusinessException이 발생하고, errorCode가 EMAIL_ALREADY_EXISTS인지 확인
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIL_ALREADY_EXISTS);

    }

    @Test
    @DisplayName("회원가입이_성공한다")
    void signup_success(){
        SignupRequest request = SignupRequest.builder()
                .email("test@test.com")
                .password("testPassword1@")
                .build();

        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);

        authService.signup(request);

        verify(passwordEncoder).encode(request.getPassword());
        verify(memberRepository).save(any(Member.class));
    }
}