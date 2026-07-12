package com.intellimarket.intellimarket.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.fromHierarchy(
                """
                ROLE_ADMIN > ROLE_SELLER
                ROLE_SELLER > ROLE_USER
                """
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RoleHierarchy roleHierarchy) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                authorize -> authorize
                        // 인증 불필요
                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/stores/**").permitAll()

                        // 역할별 권한 (RoleHierarchy 적용: ADMIN > SELLER > USER)
                        .requestMatchers("/api/admin/**").access(withRole(roleHierarchy, "ADMIN"))
                        .requestMatchers("/api/sellers/apply").access(withRole(roleHierarchy, "USER"))
                        .requestMatchers("/api/stores/me", "/api/sellers/**").access(withRole(roleHierarchy, "SELLER"))
                        .requestMatchers("/api/members/**").access(withRole(roleHierarchy, "USER"))

                        // 로그인 사용자 전체
                        .requestMatchers("/api/auth/logout", "/api/auth/me").authenticated()
                        .anyRequest().authenticated()
        );
        return http.build();
    }

    private static AuthorizationManager<RequestAuthorizationContext> withRole(RoleHierarchy roleHierarchy, String role) {
        AuthorityAuthorizationManager<RequestAuthorizationContext> manager = AuthorityAuthorizationManager.hasRole(role);
        manager.setRoleHierarchy(roleHierarchy);
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
