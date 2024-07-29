package com.example.template.domain.message.dto.response;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageParticipant;
import com.example.template.domain.message.entity.MessageThread;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDTO {
        Long messageId;
        String content;
        String imgUrl;
        Long sender;
        Long receiver;
        String readStatus;
        LocalDateTime createdAt;

        public static MessageDTO fromEntity(Message message) {
            return MessageDTO.builder()
                    .messageId(message.getId())
                    .content(message.getContent())
                    .imgUrl(message.getImgUrl())
                    .sender(message.getSender().getId())
                    .receiver(message.getReceiver().getId())
                    .readStatus(message.getReadStatus().name())
                    .createdAt(message.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDeleteDTO {
        Long messageId;
        boolean deletedBySender;
        boolean deletedByReceiver;
        LocalDateTime updatedAt;

        public static MessageDeleteDTO fromEntity(Message message) {
            return MessageDeleteDTO.builder()
                    .messageId(message.getId())
                    .deletedBySender(message.isDeletedBySen())
                    .deletedByReceiver(message.isDeletedByRec())
                    .updatedAt(message.getUpdatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreadDeleteDTO {
        Long threadId;
        Long memberId;
        String status;
        LocalDateTime leftAt;
        Long lastViewedMessageId;

        public static ThreadDeleteDTO fromEntity(MessageParticipant participant) {
            return ThreadDeleteDTO.builder()
                    .threadId(participant.getMessageThread().getId())
                    .memberId(participant.getMember().getId())
                    .status(participant.getParticipationStatus().name())
                    .leftAt(participant.getLeftAt())
                    .lastViewedMessageId(participant.getLastViewedMessage())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreadListDTO {
        Long threadId;
        String name;
        String email;
        String profileImg;
        RecentMessage recentMessage;
        int unreadMessageCount;

        public static ThreadListDTO ofEntity(MessageParticipant participant, RecentMessage recentMessage,
                                             int unreadMessageCount, Member otherMember) {
            return ThreadListDTO.builder()
                    .threadId(participant.getMessageThread().getId())
                    .name(otherMember.getName())
                    .email(otherMember.getEmail())
                    .profileImg(otherMember.getProfileImg())
                    .recentMessage(recentMessage)
                    .unreadMessageCount(unreadMessageCount)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentMessage {
        Long messageId;
        String content;
        String imgUrl;
        LocalDateTime createdAt;

        public static RecentMessage fromEntity(Message message) {
            return RecentMessage.builder()
                    .messageId(message.getId())
                    .content(message.getContent())
                    .imgUrl(message.getImgUrl())
                    .createdAt(message.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageListDTO {
        Long threadId;
        String name;
        String email;
        String profileImg;
        List<MessageDTO> messages;

        public static MessageListDTO fromEntities(MessageThread thread, Member otherParticipant, List<Message> messages) {
            List<MessageDTO> messageDTOs = messages.stream()
                    .map(MessageDTO::fromEntity)
                    .collect(Collectors.toList());

            return MessageListDTO.builder()
                    .threadId(thread.getId())
                    .name(otherParticipant.getName())
                    .email(otherParticipant.getEmail())
                    .profileImg(otherParticipant.getProfileImg())
                    .messages(messageDTOs)
                    .build();
        }
    }

}
