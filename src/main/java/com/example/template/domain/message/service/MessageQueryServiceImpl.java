package com.example.template.domain.message.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.entity.*;
import com.example.template.domain.message.entity.enums.ParticipationStatus;
import com.example.template.domain.message.entity.enums.ReadStatus;
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
    public MessageResponseDTO.MessageDTO getMessage(Long messageId, Member member) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        return MessageResponseDTO.MessageDTO.from(message);
    }

    @Override
    public List<MessageResponseDTO.ThreadListDTO> getThreadList(Member member) {
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
    public MessageResponseDTO.ThreadDTO getThread(Long writerId, Member member) {
        Member writer = memberRepository.findById(writerId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.OTHER_PARTICIPANT_NOT_FOUND));

        // 자기 자신과의 채팅방을 조회하는 경우
        if(member.equals(writer)) {
            throw new MessageException(MessageErrorCode.SELF_MESSAGE_NOT_ALLOWED);
        }

        // 게시글 작성자와의 채팅방이 존재하는지 조회
        MessageThread messageThread = messageThreadRepository.findByParticipantsMember(member, writer)
                .orElse(null);

        if (messageThread != null) {
            return MessageResponseDTO.ThreadDTO.from(messageThread);
        } else {
            return MessageResponseDTO.ThreadDTO.builder().threadId(null).build();
        }
    }

    @Override
    public MessageResponseDTO.MessageListDTO getMessageList(Long threadId, Member member) {
        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));

        // 쪽지 상대 찾기
        Member otherParticipant = getOtherParticipant(messageThread, member);

        // 채팅방 참여 상태 조회
        MessageParticipant messageParticipant = messageParticipantRepository.findByMemberAndMessageThread(member, messageThread)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND));

        // leftAt 이후의 쪽지만 조회
        List<Message> messages = messageRepository.findMessagesByMessageThreadAndMemberAndLeftAtAfter(
                messageThread, member, messageParticipant.getLeftAt());

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
