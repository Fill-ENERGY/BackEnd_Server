package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.entity.*;
import com.example.template.domain.message.exception.MessageErrorCode;
import com.example.template.domain.message.exception.MessageException;
import com.example.template.domain.message.repository.MessageParticipantRepository;
import com.example.template.domain.message.repository.MessageRepository;
import com.example.template.domain.message.repository.MessageThreadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageQueryServiceImpl implements MessageQueryService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final MessageParticipantRepository messageParticipantRepository;
    private final MessageThreadRepository messageThreadRepository;

    @Override
    public MessageResponseDTO.MessageDTO getMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        return MessageResponseDTO.MessageDTO.from(message);
    }

    @Override
    public List<MessageResponseDTO.ThreadListDTO> getThreadList() {
        // TODO 현재 로그인한 멤버 정보 받아오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        // 참여 중인 채팅방 목록 조회
        List<MessageParticipant> participantList = messageParticipantRepository.findByMemberAndParticipationStatus(member, ParticipationStatus.ACTIVE);

        return participantList.stream()
                .map(participant -> {
                    MessageThread thread = participant.getMessageThread();

                    // 최신 쪽지 조회(커스컴 쿼리 -> 멤버가 전송자 또는 수신자이면서 삭제하지 않은 쪽지)
                    Pageable pageable = PageRequest.of(0, 1);
                    List<Message> latestMessages = messageRepository.findMessagesByMessageThreadAndMemberOrderByCreatedAtDesc(thread, member, pageable);

                    MessageResponseDTO.RecentMessage recentMessage = latestMessages.stream()
                            .findFirst()
                            .map(MessageResponseDTO.RecentMessage::from)
                            .orElse(null);

                    // 받은 쪽지 중 읽지 않고 삭제하지 않은 쪽지 개수
                    long unreadMessageCount = messageRepository.countByMessageThreadAndReceiverAndReadStatusAndDeletedByRecFalse(thread, member, ReadStatus.NOT_READ);

                    // 쪽지 상대 찾기
                    Member otherParticipant = getOtherParticipant(thread, member);

                    return MessageResponseDTO.ThreadListDTO.of(participant, recentMessage, (int) unreadMessageCount, otherParticipant);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDTO.MessageListDTO getMessageList(Long threadId) {
        // TODO 현재 로그인한 멤버 정보 받아오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));

        // 쪽지 상대 찾기
        Member otherParticipant = getOtherParticipant(messageThread, member);

        // 쪽지 목록 조회
        List<Message> messages = messageRepository.findMessagesByMessageThreadAndMemberOrderByCreatedAtDesc(messageThread, member);

        return MessageResponseDTO.MessageListDTO.from(messageThread, otherParticipant, messages);
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
