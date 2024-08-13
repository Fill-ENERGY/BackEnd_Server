package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.dto.response.MessageResponseDTO;

import java.time.LocalDateTime;

public interface MessageQueryService {
    MessageResponseDTO.MessageDTO getMessage(Long messageId, Member member);

    MessageResponseDTO.ThreadListDTO getThreadList(LocalDateTime cursor, Long lastId, Integer limit, Member member);

    MessageResponseDTO.ThreadDTO getThread(Long writerId, Member member);

    MessageResponseDTO.MessageListDTO getMessageList(Long threadId, Long cursor, Integer limit, Member member);
}
