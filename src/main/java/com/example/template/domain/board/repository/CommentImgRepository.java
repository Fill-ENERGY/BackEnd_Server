package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.CommentImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentImgRepository extends JpaRepository<CommentImg, Long> {
    List<CommentImg> findAllByCommentImgUrlIn(List<String> imageUrls);
    @Query("SELECT b FROM CommentImg b WHERE b.comment IS NULL")
    List<CommentImg> findUnmappedImages();
}