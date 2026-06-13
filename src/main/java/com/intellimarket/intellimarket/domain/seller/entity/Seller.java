package com.intellimarket.intellimarket.domain.seller.entity;

import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.seller.enums.SellerStatus;
import com.intellimarket.intellimarket.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sellers")
public class Seller extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "business_registration_no", nullable = false, unique = true, length = 50)
    private String businessRegistrationNo;

    @Column(name = "representative_name", nullable = false, length = 100)
    private String representativeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_status", nullable = false, length = 30)
    private SellerStatus sellerStatus;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Builder(access = AccessLevel.PRIVATE)
    public Seller(Member member, String businessName, String businessRegistrationNo,
                  String representativeName, SellerStatus sellerStatus) {
        this.member = member;
        this.businessName = businessName;
        this.businessRegistrationNo = businessRegistrationNo;
        this.representativeName = representativeName;
        this.sellerStatus = sellerStatus;
    }

    public static Seller create(Member member, String businessName, String businessRegistrationNo, String representativeName){
        return Seller.builder()
                .member(member)
                .businessName(businessName)
                .businessRegistrationNo(businessRegistrationNo)
                .representativeName(representativeName)
                .sellerStatus(SellerStatus.PENDING)
                .build();
    }
    public void approve(LocalDateTime approvedAt) {
        this.sellerStatus = SellerStatus.APPROVED;
        this.approvedAt = approvedAt;
    }

    public void reject(String rejectionReason, LocalDateTime rejectedAt) {
        this.sellerStatus = SellerStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.rejectedAt = rejectedAt;
    }

    public void suspend() {
        this.sellerStatus = SellerStatus.SUSPENDED;
    }
}
