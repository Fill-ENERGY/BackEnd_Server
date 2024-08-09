package com.example.template.domain.message.service;

import com.example.template.domain.block.repository.BlockRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.message.dto.request.MessageRequestDTO;
import com.example.template.domain.message.dto.response.MessageResponseDTO;
import com.example.template.domain.message.entity.*;
import com.example.template.domain.message.entity.enums.ParticipationStatus;
import com.example.template.domain.message.entity.enums.ReadStatus;
import com.example.template.domain.message.exception.MessageErrorCode;
import com.example.template.domain.message.exception.MessageException;
import com.example.template.domain.message.repository.MessageImgRepository;
import com.example.template.domain.message.repository.MessageParticipantRepository;
import com.example.template.domain.message.repository.MessageRepository;
import com.example.template.domain.message.repository.MessageThreadRepository;
import com.example.template.global.config.aws.S3Manager;
import com.example.template.global.util.s3.entity.Uuid;
import com.example.template.global.util.s3.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandServiceImpl implements MessageCommandService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageThreadRepository messageThreadRepository;
    private final MessageParticipantRepository messageParticipantRepository;
    private final MessageImgRepository messageImgRepository;
    private final BlockRepository blockRepository;
    private final UuidRepository uuidRepository;
    private final S3Manager s3Manager;

    @Override
    public MessageResponseDTO.MessageImgDTO createImageMessage(List<MultipartFile> images, Member member) {
        List<String> keyNames = new ArrayList<>();
        List<Uuid> uuids = new ArrayList<>();

        // UUID 생성 및 키 이름 생성
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = Uuid.builder().uuid(uuid).build();
                uuids.add(savedUuid);
                keyNames.add(s3Manager.generateMessageKeyName(savedUuid));
            }
        }

        // UUID 일괄 저장
        uuidRepository.saveAll(uuids);

        // S3에 파일 일괄 업로드
        List<String> imageUrls = s3Manager.uploadFiles(keyNames, images);

        // MessageImg 엔티티 생성 및 저장 (Message와 연결하지 않음)
        List<MessageImg> messageImg = imageUrls.stream()
                .map(url -> MessageImg.builder().imgUrl(url).build())
                .toList();
        messageImgRepository.saveAll(messageImg);

        // MessageImgDTO 생성 및 반환
        return MessageResponseDTO.MessageImgDTO.builder()
                .images(imageUrls)
                .build();
    }

    @Override
    public MessageResponseDTO.MessageDTO createMessage(MessageRequestDTO.CreateMessageDTO requestDTO, Member sender) {
        Member receiver = memberRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new MessageException(MessageErrorCode.OTHER_PARTICIPANT_NOT_FOUND));

        // 자기 자신에게 쪽지를 보내는 경우
        if(sender.equals(receiver)) {
            throw new MessageException(MessageErrorCode.SELF_MESSAGE_NOT_ALLOWED);
        }

        // 차단된 멤버에게 쪽지를 보내는 경우
        boolean isBlockedMember = blockRepository.existsByMemberAndTargetMember(sender, receiver);
        if (isBlockedMember) {
            throw new MessageException(MessageErrorCode.BLOCKED_MEMBER_NOT_ALLOWED);
        }

        // 채팅방 조회 또는 생성
        MessageThread messageThread = getOrCreateMessageThread(requestDTO.getThreadId(), sender, receiver);

        // 쪽지 생성
        Message message = requestDTO.toEntity(sender, receiver,messageThread);

        // 이미지 처리
        if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
            List<MessageImg> messageImgs = messageImgRepository.findAllByImgUrlIn(requestDTO.getImages());

            // S3에 등록되지 않은 이미지를 가지고 접근
            if (messageImgs.size() != requestDTO.getImages().size()) {
                throw new MessageException(MessageErrorCode.INVALID_IMAGE_URLS);
            }

            messageImgs.forEach(img -> img.setMessage(message));
            messageImgRepository.saveAll(messageImgs);
        }

        Message savedMessage = messageRepository.save(message);

        return MessageResponseDTO.MessageDTO.from(savedMessage);
    }

    private MessageThread getOrCreateMessageThread(Long threadId, Member sender, Member receiver) {
        if (threadId != null) { // 채팅방이 존재하는 경우
            MessageThread messageThread = messageThreadRepository.findById(threadId)
                    .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));

            // 쪽지 전송자 또는 수신자가 채팅방을 나간 경우, 참여 상태를 다시 ACTIVE로 업데이트
            updateParticipantStatusToActive(messageThread, sender);
            updateParticipantStatusToActive(messageThread, receiver);

            // 채팅방의 updatedAt 갱신
            messageThread.setUpdatedAt(LocalDateTime.now());

            return messageThread;
        } else {    // 채팅방이 존재하지 않는 경우(첫 쪽지)
            // 채팅방 생성
            MessageThread newMessageThread = MessageThread.builder().build();
            MessageThread savedMessageThread = messageThreadRepository.save(newMessageThread);

            // 참여자 생성
            createMessageParticipant(savedMessageThread, sender);
            createMessageParticipant(savedMessageThread, receiver);

            return savedMessageThread;
        }
    }

    private void updateParticipantStatusToActive(MessageThread messageThread, Member member) {
        MessageParticipant messageParticipant = messageParticipantRepository.findByMemberAndMessageThread(member, messageThread)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND));

        if (messageParticipant.getParticipationStatus() == ParticipationStatus.LEFT) {
            messageParticipant.updateParticipationStatus(ParticipationStatus.ACTIVE);
            messageParticipantRepository.save(messageParticipant);
        }
    }

    private void createMessageParticipant(MessageThread messageThread, Member member) {
        MessageParticipant messageParticipant = MessageParticipant.builder()
                .member(member)
                .messageThread(messageThread)
                .participationStatus(ParticipationStatus.ACTIVE)
                .build();
        messageParticipantRepository.save(messageParticipant);
    }

    @Override
    public MessageResponseDTO.MessageDeleteDTO softDeleteMessage(Long messageId, Member member) {
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
        return MessageResponseDTO.MessageDeleteDTO.from(updatedMessage);
    }

    @Override
    public MessageResponseDTO.ThreadDeleteDTO softDeleteThread(Long threadId, Member member) {
        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));
        MessageParticipant participant = messageParticipantRepository.findByMemberAndMessageThread(member, messageThread)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND));

        // 받은 쪽지 중 읽지 않은 쪽지 찾기
        List<Message> unreadMessages = messageRepository.findMessagesByMessageThreadAndReceiverAndReadStatus(messageThread, member, ReadStatus.NOT_READ);

        // 읽음 상태로 업데이트
        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(message -> message.updateReadStatus(ReadStatus.READ));
            messageRepository.saveAll(unreadMessages);
        }

        // 채팅방 나가기
        participant.leaveThread();
        messageParticipantRepository.save(participant);

        return MessageResponseDTO.ThreadDeleteDTO.from(participant);
    }

    @Override
    public MessageResponseDTO.MessageListDTO updateMessageList(Long threadId, Long cursor, Integer limit, Member member) {
        // 첫 페이지 로딩 시 매우 큰 ID 값 사용
        if (cursor == 0) {
            cursor = Long.MAX_VALUE;
        }

        MessageThread messageThread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.THREAD_NOT_FOUND));

        // 쪽지 상대 찾기
        Member otherParticipant = getOtherParticipant(messageThread, member);

        // 채팅방 참여 상태 조회
        MessageParticipant messageParticipant = messageParticipantRepository.findByMemberAndMessageThread(member, messageThread)
                .orElseThrow(() -> new MessageException(MessageErrorCode.PARTICIPANT_NOT_FOUND));

        // leftAt 이후의 쪽지만 조회
        List<Message> messages = messageRepository.findMessagesByMessageThreadAndMemberAndLeftAtAfterWithCursor(
                cursor, limit, messageThread, member, messageParticipant.getLeftAt());

        Long nextCursor = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();
        boolean hasNext = messages.size() == limit;

        // 읽음 상태 업데이트 전 dto 생성(안읽은 쪽지 색상 표시하기 위함)
        MessageResponseDTO.MessageListDTO messageListDTO = MessageResponseDTO.MessageListDTO.from(messageThread, otherParticipant, messages, nextCursor, hasNext);

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
