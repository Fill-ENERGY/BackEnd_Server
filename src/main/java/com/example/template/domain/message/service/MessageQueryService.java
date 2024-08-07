package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.response.MessageResponseDTO;

import java.util.List;

public interface MessageQueryService {
    MessageResponseDTO.MessageDTO getMessage(Long messageId);

    List<MessageResponseDTO.ThreadListDTO> getThreadList();

    MessageResponseDTO.ThreadDTO getThread(Long writerId);

    MessageResponseDTO.MessageListDTO getMessageList(Long threadId);
}
