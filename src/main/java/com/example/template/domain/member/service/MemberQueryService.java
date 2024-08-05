package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileResponseDTO;

public interface MemberQueryService {

    ProfileResponseDTO.ProfileDTO getProfile(Long memberId);

}
