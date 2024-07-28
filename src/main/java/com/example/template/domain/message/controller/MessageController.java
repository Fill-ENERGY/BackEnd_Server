package com.example.template.domain.message.controller;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.service.MessageCommandService;
import com.example.template.domain.message.service.MessageQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;

    @PostMapping("/messages")
    @Operation(summary = "쪽지 생성 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> createMessage(@Valid @RequestBody MessageRequestDTO.CreateMessageDTO requestDTO) {
        MessageResponseDTO.MessageDTO messageDTO = messageCommandService.createMessage(requestDTO);
        return ApiResponse.onSuccess(messageDTO);
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "쪽지 단일 조회 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> getMessage(@PathVariable(name = "messageId") Long messageId) {
        MessageResponseDTO.MessageDTO messageDTO= messageQueryService.getMessage(messageId);
        return ApiResponse.onSuccess(messageDTO);
    }

}
