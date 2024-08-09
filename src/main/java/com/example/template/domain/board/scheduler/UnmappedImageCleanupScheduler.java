package com.example.template.domain.board.scheduler;

import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.repository.BoardImgRepository;
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
}