package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.dto.response.MessageResponseDTO;

import java.util.List;

public interface MessageQueryService {
    MessageResponseDTO.MessageDTO getMessage(Long messageId, Member member);

    List<MessageResponseDTO.ThreadListDTO> getThreadList(Member member);

    MessageResponseDTO.ThreadDTO getThread(Long writerId, Member member);

    MessageResponseDTO.MessageListDTO getMessageList(Long threadId, Member member);
}
