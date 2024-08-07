package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.entity.Block;
import com.example.template.domain.block.repository.BlockRepository;
import com.example.template.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryServiceImpl implements BlockQueryService{

    private final BlockRepository blockRepository;

    @Override
    public BlockResponseDTO.BlockListDTO getBlockList(Long cursor, Integer limit, Member member) {
        // 첫 페이지 로딩 시 매우 큰 ID 값 사용
        if (cursor == 0) {
            cursor = Long.MAX_VALUE;
        }

        List<Block> blocks = blockRepository.findByMemberWithCursor(cursor, limit, member);

        Long nextCursor = blocks.isEmpty() ? null : blocks.get(blocks.size() - 1).getId();
        boolean hasNext = blocks.size() == limit;


        return BlockResponseDTO.BlockListDTO.of(blocks, nextCursor, hasNext);

    }
}
