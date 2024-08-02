package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;

public interface MessageCommandService {
    MessageResponseDTO.MessageDTO createMessage(MessageRequestDTO.CreateMessageDTO requestDTO);

    MessageResponseDTO.MessageDeleteDTO softDeleteMessage(Long messageId);

    MessageResponseDTO.ThreadDeleteDTO softDeleteThread(Long threadId);

    MessageResponseDTO.MessageListDTO updateMessageList(Long threadId);
}
