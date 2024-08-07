package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.entity.Block;
import com.example.template.domain.block.repository.BlockRepository;
import com.example.template.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryServiceImpl implements BlockQueryService{

    private final BlockRepository blockRepository;

    @Override
    public List<BlockResponseDTO.BlockListDTO> getBlockList(Member member) {
        List<Block> blockList = blockRepository.findByMember(member);
        return blockList.stream()
                .map(BlockResponseDTO.BlockListDTO::from)
                .collect(Collectors.toList());

    }
}
