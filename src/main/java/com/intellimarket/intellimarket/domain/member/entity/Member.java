package com.intellimarket.intellimarket.domain.member.entity;

import com.intellimarket.intellimarket.domain.member.enums.Gender;
import com.intellimarket.intellimarket.domain.member.enums.MemberRole;
import com.intellimarket.intellimarket.domain.member.enums.MemberStatus;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 30)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false, length = 30)
    private MemberStatus memberStatus;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String email, String passwordHash, String name, Integer birthYear,
                  Gender gender, MemberRole memberRole, MemberStatus memberStatus) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.birthYear = birthYear;
        this.gender = gender;
        this.memberRole = memberRole;
        this.memberStatus = memberStatus;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void changeStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    public void changeRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public void updatePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
