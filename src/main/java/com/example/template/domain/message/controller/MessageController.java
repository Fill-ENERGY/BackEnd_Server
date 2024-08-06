package com.example.template.domain.message.controller;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.service.MessageCommandService;
import com.example.template.domain.message.service.MessageQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;

    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "쪽지 생성 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> createMessage(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    @Valid @RequestPart("requestDTO") MessageRequestDTO.CreateMessageDTO requestDTO) {
        MessageResponseDTO.MessageDTO messageDTO = messageCommandService.createMessage(files, requestDTO);
        return ApiResponse.onSuccess(messageDTO);
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "쪽지 단일 조회 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> getMessage(@PathVariable("messageId") Long messageId) {
        MessageResponseDTO.MessageDTO messageDTO= messageQueryService.getMessage(messageId);
        return ApiResponse.onSuccess(messageDTO);
    }

    @GetMapping("/threads/{threadId}/messages")
    @Operation(summary = "쪽지 목록 조회 API", description = "채팅방의 쪽지 목록을 조회합니다.")
    public ApiResponse<MessageResponseDTO.MessageListDTO> getMessageList(@PathVariable("threadId") Long threadId) {
        MessageResponseDTO.MessageListDTO messageListDTO= messageQueryService.getMessageList(threadId);
        return ApiResponse.onSuccess(messageListDTO);
    }

    @PatchMapping("/threads/{threadId}/messages")
    @Operation(summary = "쪽지 목록 조회 및 읽음 상태 업데이트 API", description = "채팅방의 쪽지 목록을 조회하고 읽지 않은 쪽지를 읽음으로 업데이트합니다.")
    public ApiResponse<MessageResponseDTO.MessageListDTO> updateMessageList(@PathVariable("threadId") Long threadId) {
        MessageResponseDTO.MessageListDTO messageListDTO= messageCommandService.updateMessageList(threadId);
        return ApiResponse.onSuccess(messageListDTO);
    }

    @GetMapping("/threads")
    @Operation(summary = "채팅방 목록 조회 API")
    public ApiResponse<List<MessageResponseDTO.ThreadListDTO>> getThreadList() {
        List<MessageResponseDTO.ThreadListDTO> threadListDTO= messageQueryService.getThreadList();
        return ApiResponse.onSuccess(threadListDTO);
    }

    @PatchMapping("/messages/{messageId}")
    @Operation(summary = "쪽지 삭제(soft delete) API", description = "쪽지를 논리적으로 삭제합니다. 쪽지를 삭제된 것으로 표시합니다.")
    public ApiResponse<MessageResponseDTO.MessageDeleteDTO> softDeleteMessage(@PathVariable("messageId") Long messageId) {
        MessageResponseDTO.MessageDeleteDTO messageDeleteDTO= messageCommandService.softDeleteMessage(messageId);
        return ApiResponse.onSuccess(messageDeleteDTO);
    }

    @PatchMapping("/threads/{threadId}")
    @Operation(summary = "채팅방 나가기(soft delete) API", description = "채팅방을 논리적으로 삭제합니다. 채팅방을 나간 것으로 표시합니다.")
    public ApiResponse<MessageResponseDTO.ThreadDeleteDTO> softDeleteThread(@PathVariable("threadId") Long threadId) {
        MessageResponseDTO.ThreadDeleteDTO threadDeleteDTO= messageCommandService.softDeleteThread(threadId);
        return ApiResponse.onSuccess(threadDeleteDTO);
    }
}
