package com.example.template.domain.board.scheduler;

import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.entity.CommentImg;
import com.example.template.domain.board.repository.BoardImgRepository;
import com.example.template.domain.board.repository.CommentImgRepository;
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
    private final CommentImgRepository commentImgRepository;
    private final S3Manager s3Manager;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    @Transactional
    public void cleanupUnmappedImages() {
        // 게시글 이미지 정리
        List<BoardImg> unmappedBoardImages = boardImgRepository.findUnmappedImages();
        for (BoardImg image : unmappedBoardImages) {
            s3Manager.deleteFile(image.getBoardImgUrl());
            boardImgRepository.delete(image);
        }

        // 댓글 이미지 정리
        List<CommentImg> unmappedCommentImages = commentImgRepository.findUnmappedImages();
        for (CommentImg image : unmappedCommentImages) {
            s3Manager.deleteFile(image.getCommentImgUrl());
            commentImgRepository.delete(image);
        }

        log.info("[cleanupUnmappedImages 실행] 삭제된 게시글 이미지 수: {}, 댓글 이미지 수: {}",
                unmappedBoardImages.size(), unmappedCommentImages.size());
    }
}