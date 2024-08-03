package com.example.template.domain.member.service;

import com.example.template.domain.func1.exception.FuncException;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.apiPayload.code.GeneralErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    private final MemberRepository memberRepository;

    @Override
    public ProfileResponseDTO.ProfileDTO getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FuncException(GeneralErrorCode.NOT_FOUND_404));
        return ProfileResponseDTO.from(member);
    }
}