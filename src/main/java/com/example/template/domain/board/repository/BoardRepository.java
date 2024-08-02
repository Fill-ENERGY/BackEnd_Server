package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByCategoryOrderByCreatedAtDesc(Category category);
    List<Board> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<Board> findByIdAndMemberId(Long id, Long memberId);
}
