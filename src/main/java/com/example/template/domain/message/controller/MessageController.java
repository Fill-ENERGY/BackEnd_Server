package com.example.template.domain.message.controller;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.scheduler.MessageHardDeleteScheduler;
import com.example.template.domain.message.service.MessageCommandService;
import com.example.template.domain.message.service.MessageQueryService;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;
    private final MessageHardDeleteScheduler messageHardDeleteScheduler;

    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "쪽지 생성 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> createMessage(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    @Valid @RequestPart("requestDTO") MessageRequestDTO.CreateMessageDTO requestDTO,
                                                                    @AuthenticatedMember Member member) {
        MessageResponseDTO.MessageDTO messageDTO = messageCommandService.createMessage(files, requestDTO, member);
        return ApiResponse.onSuccess(messageDTO);
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "쪽지 단일 조회 API")
    public ApiResponse<MessageResponseDTO.MessageDTO> getMessage(@PathVariable("messageId") Long messageId, @AuthenticatedMember Member member) {
        MessageResponseDTO.MessageDTO messageDTO= messageQueryService.getMessage(messageId, member);
        return ApiResponse.onSuccess(messageDTO);
    }

    // TODO: 페이지네이션
    @GetMapping("/threads/{threadId}/messages")
    @Operation(summary = "쪽지 목록 조회 API", description = "채팅방의 쪽지 목록을 조회합니다.")
    public ApiResponse<MessageResponseDTO.MessageListDTO> getMessageList(@PathVariable("threadId") Long threadId, @AuthenticatedMember Member member) {
        MessageResponseDTO.MessageListDTO messageListDTO= messageQueryService.getMessageList(threadId, member);
        return ApiResponse.onSuccess(messageListDTO);
    }

    // TODO: 페이지네이션
    @PatchMapping("/threads/{threadId}/messages")
    @Operation(summary = "쪽지 목록 조회 및 읽음 상태 업데이트 API", description = "채팅방의 쪽지 목록을 조회하고 읽지 않은 쪽지를 읽음으로 업데이트합니다.")
    public ApiResponse<MessageResponseDTO.MessageListDTO> updateMessageList(@PathVariable("threadId") Long threadId, @AuthenticatedMember Member member) {
        MessageResponseDTO.MessageListDTO messageListDTO= messageCommandService.updateMessageList(threadId, member);
        return ApiResponse.onSuccess(messageListDTO);
    }

    @GetMapping("/threads/members/{memberId}")
    @Operation(summary = "채팅방 조회 API (커뮤니티 > 채팅하기)", description = "게시글 작성자의 id를 전달해주세요.\n\n" +
            "커뮤니티에서 채팅하기를 클릭하는 경우, 게시글 작성자와의 채팅방이 존재하는지 조회합니다.\n\n" + "존재하면 채팅방 id를, 존재하지 않으면 null을 반환합니다.")
    public ApiResponse<MessageResponseDTO.ThreadDTO> getThread(@PathVariable("memberId") Long writerId, @AuthenticatedMember Member member) {
        MessageResponseDTO.ThreadDTO threadDTO = messageQueryService.getThread(writerId, member);
        return ApiResponse.onSuccess(threadDTO);
    }

    @GetMapping("/threads")
    @Operation(summary = "채팅방 목록 조회 API")
    @Parameters({
            @Parameter(name = "cursor", description = "마지막 채팅방의 updatedAt 값. 첫 페이지 조회 시에는 값을 비워서 전달해주세요."),
            @Parameter(name = "lastId", description = "마지막 채팅방의 id(threadId), default = 0"),
            @Parameter(name = "limit", description = "가져올 채팅방 개수, default = 10"),
    })
    public ApiResponse<MessageResponseDTO.ThreadListDTO> getThreadList(@RequestParam(required = false) String cursor,
                                                                       @RequestParam(defaultValue = "0") Long lastId,
                                                                       @RequestParam(defaultValue = "10") Integer limit,
                                                                       @AuthenticatedMember Member member) {

        // 첫 페이지 로딩 시 매우 미래의 시간을 커서로, 매우 큰 값으로 lastId 설정
        LocalDateTime parsedCursor = cursor != null ? LocalDateTime.parse(cursor, DateTimeFormatter.ISO_DATE_TIME) : LocalDateTime.now().plusYears(100);
        if (lastId == 0) {
            lastId = Long.MAX_VALUE;
        }
        MessageResponseDTO.ThreadListDTO threadListDTO = messageQueryService.getThreadList(parsedCursor, lastId, limit, member);
        return ApiResponse.onSuccess(threadListDTO);
    }

    @PatchMapping("/messages/{messageId}")
    @Operation(summary = "쪽지 삭제(soft delete) API", description = "쪽지를 논리적으로 삭제합니다. 쪽지를 삭제된 것으로 표시합니다.")
    public ApiResponse<MessageResponseDTO.MessageDeleteDTO> softDeleteMessage(@PathVariable("messageId") Long messageId, @AuthenticatedMember Member member) {
        MessageResponseDTO.MessageDeleteDTO messageDeleteDTO= messageCommandService.softDeleteMessage(messageId, member);
        return ApiResponse.onSuccess(messageDeleteDTO);
    }

    @PatchMapping("/threads/{threadId}")
    @Operation(summary = "채팅방 나가기(soft delete) API", description = "채팅방을 논리적으로 삭제합니다. 채팅방을 나간 것으로 표시합니다.")
    public ApiResponse<MessageResponseDTO.ThreadDeleteDTO> softDeleteThread(@PathVariable("threadId") Long threadId, @AuthenticatedMember Member member) {
        MessageResponseDTO.ThreadDeleteDTO threadDeleteDTO= messageCommandService.softDeleteThread(threadId, member);
        return ApiResponse.onSuccess(threadDeleteDTO);
    }

    // TODO: 스케줄러 테스트용 api
    @PostMapping("/messages/hard-delete-thread")
    @Operation(summary = "채팅방 삭제(hard delete) 스케쥴러 테스트용 API", description = "(연동x) 채팅방을 물리적으로 삭제합니다. 참여자가 없는 채팅방을 삭제합니다.")
    public ApiResponse<String> triggerHardDeleteThread() {
        messageHardDeleteScheduler.hardDeleteThread();
        return ApiResponse.onSuccess("채팅방 삭제 스케줄러 호출");
    }

    @PostMapping("/messages/hard-delete-message")
    @Operation(summary = "쪽지 삭제(hard delete) 스케쥴러 테스트용 API", description = "(연동x) 쪽지를 물리적으로 삭제합니다. 보낸 사람과 받는 사람이 모두 삭제한 쪽지를 삭제합니다.")
    public ApiResponse<String> triggerHardDeleteMessage() {
        messageHardDeleteScheduler.hardDeleteMessage();
        return ApiResponse.onSuccess("쪽지 삭제 스케줄러 호출");
    }
}
