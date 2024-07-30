package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
    void deleteByBoardId(Long boardId);
}
