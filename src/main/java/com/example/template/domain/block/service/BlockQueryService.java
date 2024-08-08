package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.member.entity.Member;


public interface BlockQueryService {
    BlockResponseDTO.BlockListDTO getBlockList(Long cursor, Integer limit, Member member);
}
