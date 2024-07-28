package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.response.MessageResponseDTO;

public interface MessageQueryService {
    MessageResponseDTO.MessageDTO getMessage(Long messageId);
}
