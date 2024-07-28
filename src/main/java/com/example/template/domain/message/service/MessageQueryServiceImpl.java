package com.example.template.domain.message.service;

import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.exception.MessageErrorCode;
import com.example.template.domain.message.exception.MessageException;
import com.example.template.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageQueryServiceImpl implements MessageQueryService{

    private final MessageRepository messageRepository;

    @Override
    public MessageResponseDTO.MessageDTO getMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        return MessageResponseDTO.MessageDTO.fromEntity(message);
    }
}
