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

    @Getter
    @Builder
    public static class MessageImgDTO {
        private List<String> images;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDTO {
        private Long messageId;
        private Long threadId;
        private String content;
        private List<String> images;
        private Long sender;
        private Long receiver;
        private String readStatus;
        private LocalDateTime createdAt;

        public static MessageDTO from(Message message) {
            return MessageDTO.builder()
                    .messageId(message.getId())
                    .threadId(message.getMessageThread().getId())
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

        public static ThreadDeleteDTO from(MessageParticipant participant) {
            return ThreadDeleteDTO.builder()
                    .threadId(participant.getMessageThread().getId())
                    .memberId(participant.getMember().getId())
                    .status(participant.getParticipationStatus().name())
                    .leftAt(participant.getLeftAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreadDetailListDTO {
        private Long threadId;
        private String name;
        private String nickname;
        private String profileImg;
        private RecentMessage recentMessage;
        private int unreadMessageCount;
        private LocalDateTime updatedAt;

        public static ThreadDetailListDTO of(MessageParticipant participant, RecentMessage recentMessage,
                                             int unreadMessageCount, Member otherMember) {
            return ThreadDetailListDTO.builder()
                    .threadId(participant.getMessageThread().getId())
                    .name(otherMember.getName())
                    .nickname(otherMember.getNickname())
                    .profileImg(otherMember.getProfileImg())
                    .recentMessage(recentMessage)
                    .unreadMessageCount(unreadMessageCount)
                    .updatedAt(participant.getMessageThread().getUpdatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreadListDTO {
        private List<ThreadDetailListDTO> threads;
        private LocalDateTime cursor;
        private Long lastId;
        private boolean hasNext;

        public static ThreadListDTO of(List<ThreadDetailListDTO> threadDetailListDTOS,
                                             LocalDateTime cursor, Long lastId, boolean hasNext) {
            return ThreadListDTO.builder()
                    .threads(threadDetailListDTOS)
                    .cursor(cursor)
                    .lastId(lastId)
                    .hasNext(hasNext)
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
        private LocalDateTime createdAt;

        public static RecentMessage from(Message message) {
            return RecentMessage.builder()
                    .messageId(message.getId())
                    .content(message.getContent() == null || message.getContent().isEmpty() ? "사진을 전송했습니다." : message.getContent())
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
        private String nickname;
        private String profileImg;
        private List<MessageDTO> messages;
        private Long nextCursor;
        private boolean hasNext;

        public static MessageListDTO from(MessageThread thread, Member otherParticipant, List<Message> messages, Long nextCursor, boolean hasNext) {
            List<MessageDTO> messageDTOs = messages.stream()
                    .map(MessageDTO::from)
                    .collect(Collectors.toList());

            return MessageListDTO.builder()
                    .threadId(thread.getId())
                    .name(otherParticipant.getName())
                    .nickname(otherParticipant.getNickname())
                    .profileImg(otherParticipant.getProfileImg())
                    .messages(messageDTOs)
                    .nextCursor(nextCursor)
                    .hasNext(hasNext)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreadDTO {
        private Long threadId;

        public static ThreadDTO from(MessageThread thread) {
            return ThreadDTO.builder()
                    .threadId(thread.getId())
                    .build();
        }
    }
}
