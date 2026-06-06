package com.intellimarket.intellimarket.domain.auth.service;

import com.intellimarket.intellimarket.common.errorcode.AuthErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.auth.dto.SignupRequest;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private  final PasswordEncoder passwordEncoder;

    //이메일 중복 체크
    public boolean checkEmailDuplicate(String email){
        return memberRepository.existsByEmail(email);
    }

    //회원가입
    public void signup(SignupRequest request){
        if(checkEmailDuplicate(request.getEmail())){
            throw new BusinessException(AuthErrorCode.EMAIL_ALEADY_EXISTS);
        }
        
        Member member = Member.create(request, passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
    }

    //로그인

}
