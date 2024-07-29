package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;

public interface BlockCommandService {
    BlockResponseDTO.BlockDTO createBlock(Long targetMemberId);

    void deleteBlock(Long blockId);
}
