package com.example.template.domain.block.dto.request;

import com.example.template.domain.block.entity.Block;
import com.example.template.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class BlockRequestDTO {

    public static Block toEntity(Member member, Member targetMember) {
        return Block.builder()
                .member(member)
                .targetMember(targetMember)
                .build();
    }
}
