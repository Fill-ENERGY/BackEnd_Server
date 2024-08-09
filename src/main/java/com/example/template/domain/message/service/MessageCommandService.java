package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageCommandService {
    MessageResponseDTO.MessageDTO createMessage(List<MultipartFile> files, MessageRequestDTO.CreateMessageDTO requestDTO, Member member);

    MessageResponseDTO.MessageDeleteDTO softDeleteMessage(Long messageId, Member member);

    MessageResponseDTO.ThreadDeleteDTO softDeleteThread(Long threadId, Member member);

    MessageResponseDTO.MessageListDTO updateMessageList(Long threadId, Long cursor, Integer limit, Member member);
}
