package com.intellimarket.intellimarket.domain.seller.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.member.enums.Gender;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.repository.SellerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepository sellerRepository;

    private MockHttpSession session;
    private SellerApplyRequest sellerApplyRequest;

    @BeforeEach
    void setUp() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@test.com")
                .password("testPassword1@")
                .name("홍길동")
                .birthYear(1980)
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        String loginBody = objectMapper.writeValueAsString(
                Map.of("email", "test@test.com", "password", "testPassword1@")
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession(false);

        sellerApplyRequest = SellerApplyRequest.builder()
                .businessName("주식회사 테스트")
                .representativeName("김대표")
                .businessRegistrationNo("123-45-67890")
                .build();
    }

    @Test
    @DisplayName("로그인한 USER가 정상 요청으로 판매자 신청에 성공한다")
    void apply_success() throws Exception {
        mockMvc.perform(post("/api/sellers/apply")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellerApplyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(sellerRepository.existsByBusinessRegistrationNo("123-45-67890")).isTrue();
    }

    @Test
    @DisplayName("이미 판매자 신청을 한 회원이 다시 신청하면 실패한다")
    void apply_fail_alreadyApplied() throws Exception {
        mockMvc.perform(post("/api/sellers/apply")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellerApplyRequest)));

        SellerApplyRequest anotherRequest = SellerApplyRequest.builder()
                .businessName("주식회사 테스트")
                .representativeName("김대표")
                .businessRegistrationNo("999-99-99999")
                .build();

        mockMvc.perform(post("/api/sellers/apply")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(anotherRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SEL002"));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 판매자 신청을 하면 실패한다")
    void apply_fail_unauthenticated() throws Exception {
        mockMvc.perform(post("/api/sellers/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellerApplyRequest)))
                .andExpect(status().isForbidden());
    }
}
