package com.intellimarket.intellimarket.domain.member.repository;

import com.intellimarket.intellimarket.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 아이디 찾기
    Optional<Member> findByEmail(String email);

    // 이메일 중복 검사
    boolean existsByEmail(String email);
}
