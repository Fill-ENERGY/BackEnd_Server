package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.member.entity.Member;

import java.util.List;

public interface BlockQueryService {
    List<BlockResponseDTO.BlockListDTO> getBlockList(Member member);
}
