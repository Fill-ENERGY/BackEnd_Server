package com.example.template.domain.message.controller;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.service.MessageService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    @Operation(summary = "쪽지 생성 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> createMessage(@Valid @RequestBody MessageRequestDTO.CreateMessageDTO requestDTO) {
        MessageResponseDTO.MessageDTO messageDTO = messageService.createMessage(requestDTO);
        return ApiResponse.onSuccess(messageDTO);
    }

}
