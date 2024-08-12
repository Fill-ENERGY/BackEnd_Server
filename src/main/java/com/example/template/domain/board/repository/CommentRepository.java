package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.board.id = :boardId")
    Optional<Comment> findByIdAndBoardId(@Param("commentId") Long commentId, @Param("boardId") Long boardId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent.id = :parentId AND c.id != :commentId")
    long countByParentIdAndIdNot(@Param("parentId") Long parentId, @Param("commentId") Long commentId);

}
