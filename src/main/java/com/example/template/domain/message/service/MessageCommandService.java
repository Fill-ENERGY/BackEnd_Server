package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageCommandService {
    MessageResponseDTO.MessageDTO createMessage(List<MultipartFile> files, MessageRequestDTO.CreateMessageDTO requestDTO);

    MessageResponseDTO.MessageDeleteDTO softDeleteMessage(Long messageId);

    MessageResponseDTO.ThreadDeleteDTO softDeleteThread(Long threadId);

    MessageResponseDTO.MessageListDTO updateMessageList(Long threadId);
}
