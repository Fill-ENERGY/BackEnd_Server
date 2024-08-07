package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.member.entity.Member;

public interface BlockCommandService {
    BlockResponseDTO.BlockDTO createBlock(Long targetMemberId, Member member);

    void deleteBlock(Long blockId, Member member);
}
