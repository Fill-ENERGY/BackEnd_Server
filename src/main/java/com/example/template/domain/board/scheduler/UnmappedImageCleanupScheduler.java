package com.example.template.domain.board.scheduler;

import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.repository.BoardImgRepository;
import com.example.template.domain.message.entity.MessageImg;
import com.example.template.domain.message.repository.MessageImgRepository;
import com.example.template.global.config.aws.S3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnmappedImageCleanupScheduler {

    private final BoardImgRepository boardImgRepository;
    private final MessageImgRepository messageImgRepository;
    private final S3Manager s3Manager;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    @Transactional
    public void cleanupUnmappedImages() {
        List<BoardImg> unmappedImages = boardImgRepository.findUnmappedImages();

        for (BoardImg image : unmappedImages) {
            s3Manager.deleteFile(image.getBoardImgUrl());
            boardImgRepository.delete(image);
        }

        log.info("[cleanupUnmappedImages 실행] 삭제완료");
    }

    @Scheduled(cron = "0 30 3 * * ?") // 매일 새벽 3시 30분에 실행
    @Transactional
    public void cleanupUnmappedMessageImages() {
        List<MessageImg> unmappedImages = messageImgRepository.findUnmappedImages();

        for (MessageImg image : unmappedImages) {
            s3Manager.deleteFile(image.getImgUrl());
            messageImgRepository.delete(image);
        }

        log.info("[cleanupUnmappedMessageImages 실행] 쪽지 이미지 삭제완료");
    }

    // TODO: 테스트용 수동 트리거 메서드 - 삭제 예정
    public void manualCleanup() {
        this.cleanupUnmappedImages();
    }
}