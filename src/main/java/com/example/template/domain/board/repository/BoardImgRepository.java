package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.BoardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardImgRepository extends JpaRepository<BoardImg, Long> {
    List<BoardImg> findAllByBoardImgUrlIn(List<String> imageUrls);
    @Query("SELECT b FROM BoardImg b WHERE b.board IS NULL")
    List<BoardImg> findUnmappedImages();
}
