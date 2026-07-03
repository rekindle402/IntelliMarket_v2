package com.intellimarket.intellimarket.domain.seller.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.enums.Gender;
import com.intellimarket.intellimarket.domain.member.enums.MemberRole;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import com.intellimarket.intellimarket.domain.seller.dto.SellerApplyRequest;
import com.intellimarket.intellimarket.domain.seller.entity.Seller;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SellerAdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private SellerRepository sellerRepository;

    private MockHttpSession adminSession;
    private Long savedSellerId;

    @BeforeEach
    void setUp() throws Exception {
        // 일반 회원 가입 후 판매자 신청
        SignupRequest userSignup = SignupRequest.builder()
                .email("seller@test.com")
                .password("testPassword1@")
                .name("판매자")
                .birthYear(1990)
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignup)));

        String userLoginBody = objectMapper.writeValueAsString(
                Map.of("email", "seller@test.com", "password", "testPassword1@"));

        MvcResult userResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userLoginBody))
                .andReturn();

        MockHttpSession userSession = (MockHttpSession) userResult.getRequest().getSession(false);

        SellerApplyRequest applyRequest = SellerApplyRequest.builder()
                .businessName("주식회사 테스트")
                .representativeName("김대표")
                .businessRegistrationNo("123-45-67890")
                .build();

        mockMvc.perform(post("/api/sellers/apply")
                .session(userSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyRequest)));

        savedSellerId = sellerRepository.findByMemberId(
                memberRepository.findByEmail("seller@test.com").get().getId()
        ).get().getId();

        // ADMIN 회원 직접 저장 후 로그인
        SignupRequest adminSignup = SignupRequest.builder()
                .email("admin@test.com")
                .password("testPassword1@")
                .name("관리자")
                .birthYear(1985)
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminSignup)));

        Member admin = memberRepository.findByEmail("admin@test.com").get();
        admin.changeRole(MemberRole.ADMIN);

        String adminLoginBody = objectMapper.writeValueAsString(
                Map.of("email", "admin@test.com", "password", "testPassword1@"));

        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminLoginBody))
                .andReturn();

        adminSession = (MockHttpSession) adminResult.getRequest().getSession(false);
    }

    @Test
    @DisplayName("관리자가 판매자 목록을 조회하면 전체 목록이 반환된다")
    void getSellerList_success() throws Exception {
        mockMvc.perform(get("/api/admin/sellers")
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("관리자가 상태 필터로 목록을 조회하면 해당 상태의 판매자만 반환된다")
    void getSellerList_filteredByStatus() throws Exception {
        mockMvc.perform(get("/api/admin/sellers")
                .param("status", "PENDING")
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].sellerStatus").value("PENDING"));
    }

    @Test
    @DisplayName("관리자가 판매자 상세를 조회하면 해당 판매자 정보가 반환된다")
    void getSellerDetail_success() throws Exception {
        mockMvc.perform(get("/api/admin/sellers/{sellerId}", savedSellerId)
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sellerId").value(savedSellerId));
    }

    @Test
    @DisplayName("관리자가 판매자를 승인하면 상태가 APPROVED로 변경된다")
    void approveSeller_success() throws Exception {
        mockMvc.perform(patch("/api/admin/sellers/{sellerId}/approve", savedSellerId)
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Seller seller = sellerRepository.findById(savedSellerId).get();
        assertThat(seller.getSellerStatus().name()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("관리자가 판매자를 거절하면 상태가 REJECTED로 변경되고 거절 사유가 저장된다")
    void rejectSeller_success() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("rejectionReason", "서류 미비"));

        mockMvc.perform(patch("/api/admin/sellers/{sellerId}/reject", savedSellerId)
                .session(adminSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Seller seller = sellerRepository.findById(savedSellerId).get();
        assertThat(seller.getSellerStatus().name()).isEqualTo("REJECTED");
        assertThat(seller.getRejectionReason()).isEqualTo("서류 미비");
    }
}
