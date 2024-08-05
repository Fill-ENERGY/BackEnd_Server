package com.example.template.domain.message.dto.request;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageThread;
import com.example.template.domain.message.entity.ReadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MessageRequestDTO {

    @Getter
    public static class CreateMessageDTO {
        Long threadId;
        String content;
        String imgUrl;
        @NotNull(message = "받는 사람 id는 필수입니다.")
        Long receiverId;

        public Message toEntity(Member sender, Member receiver, MessageThread messageThread) {
            return Message.builder()
                    .content(content)
                    .imgUrl(imgUrl)
                    .readStatus(ReadStatus.NOT_READ)
                    .deletedBySen(false)
                    .deletedByRec(false)
                    .sender(sender)
                    .receiver(receiver)
                    .messageThread(messageThread)
                    .build();
        }
    }
}
