package com.intellimarket.intellimarket.domain.auth.listener;

import com.intellimarket.intellimarket.domain.auth.security.CustomUserDetails;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginSuccessListener {

    private final MemberRepository memberRepository;

    @EventListener
    @Transactional
    public void handle(AuthenticationSuccessEvent event) {
        CustomUserDetails userDetails = (CustomUserDetails) event.getAuthentication().getPrincipal();

        Member member = memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow();

        member.updateLastLoginAt(LocalDateTime.now());
    }
}
