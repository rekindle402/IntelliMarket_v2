package com.intellimarket.intellimarket.domain.member.service;

import com.intellimarket.intellimarket.common.errorcode.MemberErrorCode;
import com.intellimarket.intellimarket.common.exception.BusinessException;
import com.intellimarket.intellimarket.domain.member.dto.MemberProfileResponse;
import com.intellimarket.intellimarket.domain.member.dto.MemberUpdateRequest;
import com.intellimarket.intellimarket.domain.member.entity.Member;
import com.intellimarket.intellimarket.domain.member.enums.MemberStatus;
import com.intellimarket.intellimarket.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberProfileResponse getMyProfile(Long memberId) {
        return MemberProfileResponse.from(getMember(memberId));
    }

    public MemberProfileResponse updateMyProfile(Long memberId, MemberUpdateRequest request) {
        Member member = getMember(memberId);
        member.updateProfile(request.getName(), request.getBirthYear(), request.getGender());
        return MemberProfileResponse.from(member);
    }

    public void withdraw(Long memberId) {
        Member member = getMember(memberId);
        member.changeStatus(MemberStatus.WITHDRAWN);
    }

    public Page<MemberProfileResponse> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberProfileResponse::from);
    }

    public MemberProfileResponse getMemberDetail(Long memberId) {
        return MemberProfileResponse.from(getMember(memberId));
    }

    public void changeMemberStatus(Long memberId, MemberStatus status) {
        Member member = getMember(memberId);
        member.changeStatus(status);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
