package com.example.template.domain.message.dto.response;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageImg;
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
        private Long messageId;
        private String content;
        private List<String> images;
        private Long sender;
        private Long receiver;
        private String readStatus;
        private LocalDateTime createdAt;

        public static MessageDTO from(Message message) {
            return MessageDTO.builder()
                    .messageId(message.getId())
                    .content(message.getContent())
                    .images(message.getImages().stream().map(MessageImg::getImgUrl).toList())
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
        private Long messageId;
        private boolean deletedBySender;
        private boolean deletedByReceiver;
        private LocalDateTime updatedAt;

        public static MessageDeleteDTO from(Message message) {
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
        private Long threadId;
        private Long memberId;
        private String status;
        private LocalDateTime leftAt;
        private Long lastViewedMessageId;

        public static ThreadDeleteDTO from(MessageParticipant participant) {
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
        private Long threadId;
        private String name;
        private String email;
        private String profileImg;
        private RecentMessage recentMessage;
        private int unreadMessageCount;

        public static ThreadListDTO of(MessageParticipant participant, RecentMessage recentMessage,
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
        private Long messageId;
        private String content;
//        private String imgUrl;
        private LocalDateTime createdAt;

        public static RecentMessage from(Message message) {
            return RecentMessage.builder()
                    .messageId(message.getId())
                    .content(message.getContent())
//                    .imgUrl(message.getImgUrl())
                    .createdAt(message.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageListDTO {
        private Long threadId;
        private String name;
        private String email;
        private String profileImg;
        private List<MessageDTO> messages;

        public static MessageListDTO from(MessageThread thread, Member otherParticipant, List<Message> messages) {
            List<MessageDTO> messageDTOs = messages.stream()
                    .map(MessageDTO::from)
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
