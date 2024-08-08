package com.example.template.domain.message.scheduler;

import com.example.template.domain.message.entity.Message;
import com.example.template.domain.message.entity.MessageImg;
import com.example.template.domain.message.entity.MessageThread;
import com.example.template.domain.message.repository.MessageImgRepository;
import com.example.template.domain.message.repository.MessageParticipantRepository;
import com.example.template.domain.message.repository.MessageRepository;
import com.example.template.domain.message.repository.MessageThreadRepository;
import com.example.template.global.config.aws.S3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageHardDeleteScheduler {

    private final MessageRepository messageRepository;
    private final MessageThreadRepository messageThreadRepository;
    private final MessageParticipantRepository messageParticipantRepository;
    private final MessageImgRepository messageImgRepository;
    private final S3Manager s3Manager;

    // 채팅방 hard delete - 참여자 둘다 참여 상태가 LEFT인 채팅방 (+ 참여자 정보, 쪽지, 이미지) 삭제
    @Scheduled(cron = "0 30 0 * * ?") // 매일 00:30에 실행
    public void hardDeleteThread() {
        log.info("[hardDeleteThread 실행] 참여자가 없는 채팅방을 삭제합니다.");

        // 참여자 두명 모두 참여 상태가 LEFT인 채팅방 조회
        List<MessageThread> threads = messageThreadRepository.findAllThreadsWithAllParticipantsLeft();

        for (MessageThread thread : threads) {
            try {
                // 채팅방의 쪽지 목록 조회
                List<Message> messages = messageRepository.findByMessageThreadId(thread.getId());

                // 쪽지 및 이미지 삭제
                deleteMessagesAndImages(messages);

                // 참여자 삭제
                messageParticipantRepository.deleteByMessageThreadId(thread.getId());

                // 채팅방 삭제
                messageThreadRepository.deleteById(thread.getId());

                log.info("채팅방을 성공적으로 삭제했습니다. ID: {}", thread.getId());
            } catch (Exception e) {
                log.error("채팅방 삭제에 실패했습니다. ID: {}", thread.getId(), e);
            }
        }
    }

    // 쪽지 hard delete - sender, receiver가 둘다 삭제한 쪽지 (+ 이미지) 삭제
    @Scheduled(cron = "0 45 0 * * ?") // 매일 00:45에 실행
    public void hardDeleteMessage() {
        log.info("[hardDeleteMessage 실행] 보낸 사람과 받는 사람이 모두 삭제한 쪽지를 삭제합니다.");

        // 삭제할 쪽지 목록
        List<Message> messages = messageRepository.findByDeletedBySenTrueAndDeletedByRecTrue();

        // 쪽지 및 이미지 삭제
        deleteMessagesAndImages(messages);
    }

    private void deleteMessagesAndImages(List<Message> messages) {
        // 쪽지 이미지 삭제
        for (Message message : messages) {
            try {
                if (!message.getImages().isEmpty()) {
                    // S3에서 이미지 삭제
                    List<String> imageUrls = message.getImages().stream()
                            .map(MessageImg::getImgUrl)
                            .collect(Collectors.toList());
                    s3Manager.deleteFiles(imageUrls);

                    // 이미지 엔티티 삭제
                    messageImgRepository.deleteAll(message.getImages());
                }

                // 쪽지 삭제
                messageRepository.delete(message);
                log.info("쪽지를 성공적으로 삭제했습니다. ID: {}", message.getId());
            } catch (Exception e) {
                log.error("쪽지 삭제에 실패했습니다. ID: {}", message.getId(), e);
            }
        }
    }

}
