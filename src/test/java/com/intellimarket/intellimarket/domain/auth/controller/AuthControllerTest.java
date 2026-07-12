package com.intellimarket.intellimarket.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.member.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "testPassword1@";

    private SignupRequest defaultSignupRequest() {
        return SignupRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name("홍길동")
                .birthYear(1980)
                .gender(Gender.MALE)
                .build();
    }

    private void signup(SignupRequest request) throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private MockHttpSession login(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", email, "password", password)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    @DisplayName("정상적인_요청으로_회원가입에_성공한다")
    void signup_success() throws Exception {
        SignupRequest request = defaultSignupRequest();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("이미_가입된_이메일로_회원가입을_시도하면_실패한다")
    void signup_fail_duplicateEmail() throws Exception {
        signup(defaultSignupRequest());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultSignupRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AUT001"));
    }

    @Test
    @DisplayName("이메일_형식이_올바르지_않으면_회원가입에_실패한다")
    void signup_fail_invalidEmailFormat() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("invalid-email")
                .password(PASSWORD)
                .name("홍길동")
                .birthYear(1980)
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COM001"));
    }

    @Test
    @DisplayName("올바른_이메일과_비밀번호로_로그인에_성공한다")
    void login_success() throws Exception {
        signup(defaultSignupRequest());

        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", EMAIL, "password", PASSWORD)
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @Test
    @DisplayName("비밀번호가_일치하지_않으면_로그인에_실패한다")
    void login_fail_wrongPassword() throws Exception {
        signup(defaultSignupRequest());

        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", EMAIL, "password", "wrongPassword1@")
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUT002"));
    }

    @Test
    @DisplayName("존재하지_않는_이메일로_로그인하면_실패한다")
    void login_fail_emailNotFound() throws Exception {
        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", "notexist@test.com", "password", PASSWORD)
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUT002"));
    }

    @Test
    @DisplayName("로그인한_사용자는_내_정보를_조회할_수_있다")
    void me_success() throws Exception {
        signup(defaultSignupRequest());
        MockHttpSession session = login(EMAIL, PASSWORD);

        mockMvc.perform(get("/api/auth/me")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @Test
    @DisplayName("로그인하지_않은_상태로_내_정보를_조회하면_실패한다")
    void me_fail_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그아웃에_성공하면_이후_세션으로_내_정보를_조회할_수_없다")
    void logout_success() throws Exception {
        signup(defaultSignupRequest());
        MockHttpSession session = login(EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/logout")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/auth/me")
                .session(session))
                .andExpect(status().isForbidden());
    }
}
