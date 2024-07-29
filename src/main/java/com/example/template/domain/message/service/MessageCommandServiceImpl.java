package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.entity.*;
import com.example.template.domain.message.exception.MessageErrorCode;
import com.example.template.domain.message.exception.MessageException;
import com.example.template.domain.message.repository.MessageParticipantRepository;
import com.example.template.domain.message.repository.MessageRepository;
import com.example.template.domain.message.repository.MessageThreadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandServiceImpl implements MessageCommandService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageThreadRepository messageThreadRepository;
    private final MessageParticipantRepository messageParticipantRepository;

    @Override
    public MessageResponseDTO.MessageDTO createMessage(MessageRequestDTO.CreateMessageDTO requestDTO) {
        // TODO 현재 로그인한 멤버 정보 받아오기, 멤버 관련 예외 처리로 변경하기
        Member sender = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Member receiver = memberRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        MessageThread messageThread = null;
        if (requestDTO.getThreadId() != null) { // 채팅방이 존재하는 경우
            messageThread = messageThreadRepository.findById(requestDTO.getThreadId())
                    .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));
        } else {    // 채팅방이 존재하지 않는 경우(첫 쪽지)
            // 채팅방 생성
            MessageThread newMessageThread = MessageThread.builder().build();
            messageThread = messageThreadRepository.save(newMessageThread);

            // 참여자 생성
            MessageParticipant senderParticipant = MessageParticipant.builder()
                    .member(sender)
                    .messageThread(messageThread)
                    .participationStatus(ParticipationStatus.ACTIVE)
                    .build();
            messageParticipantRepository.save(senderParticipant);

            MessageParticipant receiverParticipant = MessageParticipant.builder()
                    .member(receiver)
                    .messageThread(messageThread)
                    .participationStatus(ParticipationStatus.ACTIVE)
                    .build();
            messageParticipantRepository.save(receiverParticipant);
        }

        // 쪽지 생성
        // TODO S3 구현 후 사진 저장 로직 추가 예정
        Message message = requestDTO.toEntity(sender, receiver, messageThread);
        Message savedMessage = messageRepository.save(message);

        return MessageResponseDTO.MessageDTO.fromEntity(savedMessage);
    }

    @Override
    public MessageResponseDTO.MessageDeleteDTO softDeleteMessage(Long messageId) {
        // TODO 현재 로그인한 멤버 정보 받아오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        // sender, receiver 여부 확인
        if (message.getSender().getId().equals(member.getId())) {
            message.updateDeletedBySender(true);
        } else if (message.getReceiver().getId().equals(member.getId())){
            message.updateDeletedByReceiver(true);
        } else {
            throw new MessageException(MessageErrorCode.PERMISSION_DENIED);
        }

        Message updatedMessage = messageRepository.save(message);
        return MessageResponseDTO.MessageDeleteDTO.fromEntity(updatedMessage);
    }

    @Override
    public MessageResponseDTO.ThreadDeleteDTO softDeleteThread(Long threadId) {
        // TODO 현재 로그인한 멤버 정보 받아오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));
        MessageParticipant participant = messageParticipantRepository.findByMemberAndMessageThread(member, messageThread)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND));

        // 마지막으로 본 메시지 id 업데이트
        Optional<Message> optionalLastViewedMessage = messageRepository.findTopByReceiverAndReadStatusAndDeletedByRecFalseOrderByCreatedAtDesc(member, ReadStatus.READ);
        if (optionalLastViewedMessage.isPresent()) {
            participant.leaveThread(optionalLastViewedMessage.get().getId());
        } else {
            participant.leaveThread(null);
        }
        messageParticipantRepository.save(participant);

        return MessageResponseDTO.ThreadDeleteDTO.fromEntity(participant);
    }

    @Override
    public MessageResponseDTO.MessageListDTO updateMessageList(Long threadId) {
        // TODO 현재 로그인한 멤버 정보 받아오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));

        // 쪽지 상대 찾기
        Member otherParticipant = getOtherParticipant(messageThread, member);

        // 쪽지 목록 조회
        List<Message> messages = messageRepository.findMessagesByMessageThreadAndMemberOrderByCreatedAtDesc(messageThread, member);

        // 읽음 상태 업데이트 전 dto 생성(안읽은 쪽지 색상 표시하기 위함)
        MessageResponseDTO.MessageListDTO messageListDTO = MessageResponseDTO.MessageListDTO.fromEntities(messageThread, otherParticipant, messages);

        // 읽지 않은 쪽지 상태 업데이트
        List<Message> unreadMessages = messages.stream()
                .filter(message -> message.getReceiver().equals(member) && message.getReadStatus() == ReadStatus.NOT_READ)
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(message -> message.updateReadStatus(ReadStatus.READ));
            messageRepository.saveAll(unreadMessages);
        }

        return messageListDTO;
    }

    private static Member getOtherParticipant(MessageThread thread, Member member) {
        Member otherParticipant = null;
        for (MessageParticipant p : thread.getParticipants()) {
            if (!p.getMember().equals(member)) {
                otherParticipant = p.getMember();
                break;
            }
        }

        if (otherParticipant == null) {
            throw new MessageException(MessageErrorCode.OTHER_PARTICIPANT_NOT_FOUND);
        }
        return otherParticipant;
    }
}
