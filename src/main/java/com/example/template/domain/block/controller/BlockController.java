package com.example.template.domain.block.controller;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.service.BlockCommandService;
import com.example.template.domain.block.service.BlockQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BlockController {

    private final BlockCommandService blockCommandService;
    private final BlockQueryService blockQueryService;

    @PostMapping("/blocks/members/{targetMemberId}")
    @Operation(summary = "차단하기 API", description = "차단할 멤버의 아이디를 전달해주세요.")
    public ApiResponse<BlockResponseDTO.BlockDTO> createBlock(@PathVariable(name = "targetMemberId") Long targetMemberId) {
        BlockResponseDTO.BlockDTO blockDTO = blockCommandService.createBlock(targetMemberId);
        return ApiResponse.onSuccess(blockDTO);
    }

    @GetMapping("/blocks/members")
    @Operation(summary = "차단 목록 조회 API", description = "멤버가 차단한 목록을 조회합니다. 차단 목록이 없으면 빈 배열을 반환합니다.")
    public ApiResponse<List<BlockResponseDTO.BlockListDTO>> getBlockList() {
        List<BlockResponseDTO.BlockListDTO> blockListDTO = blockQueryService.getBlockList();
        return ApiResponse.onSuccess(blockListDTO);
    }

    @DeleteMapping("/blocks/{blockId}")
    @Operation(summary = "차단 해제 API")
    public ApiResponse<Void> deleteBlock(@PathVariable(name = "blockId") Long blockId) {
        blockCommandService.deleteBlock(blockId);
        return ApiResponse.onSuccess(null);
    }
}
