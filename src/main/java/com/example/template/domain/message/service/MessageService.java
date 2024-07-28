package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;

public interface MessageService {
    MessageResponseDTO.MessageDTO createMessage(MessageRequestDTO.CreateMessageDTO requestDTO);
}
