package com.example.template.domain.block.dto.response;

import com.example.template.domain.block.entity.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BlockResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockDTO {
        Long blockId;
        Long memberId;  // 멤버 아이디
        Long targetMemberId;    // 차단 대상 멤버 아이디

        public static BlockDTO from(Block block) {
            return BlockDTO.builder()
                    .blockId(block.getId())
                    .memberId(block.getMember().getId())
                    .targetMemberId(block.getTargetMember().getId())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockListDTO {
        Long blockId;
        String name;    // 차단 대상 멤버 이름
        String email;   // 차단 대상 멤버 이메일
        String profileImg;  // 차단 대상 멤버 프로필 이미지

        public static BlockListDTO from(Block block) {
            return BlockListDTO.builder()
                    .blockId(block.getId())
                    .name(block.getTargetMember().getName())
                    .email(block.getTargetMember().getEmail())
                    .profileImg(block.getTargetMember().getProfileImg())
                    .build();
        }
    }
}
