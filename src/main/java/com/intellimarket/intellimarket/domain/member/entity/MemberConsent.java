package com.intellimarket.intellimarket.domain.member.entity;

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
@Table(name = "member_consents")
public class MemberConsent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "consent_type", nullable = false, length = 50)
    private String consentType;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "consent_version", nullable = false, length = 50)
    private String consentVersion;

    @Builder
    public MemberConsent(Member member, String consentType, Boolean isAgreed,
                         LocalDateTime agreedAt, String consentVersion) {
        this.member = member;
        this.consentType = consentType;
        this.isAgreed = isAgreed;
        this.agreedAt = agreedAt;
        this.consentVersion = consentVersion;
    }

    public void withdraw(LocalDateTime withdrawnAt) {
        this.isAgreed = false;
        this.withdrawnAt = withdrawnAt;
    }
}
