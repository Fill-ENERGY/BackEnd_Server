package com.example.template.domain.board.repository;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardLike;
import com.example.template.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByMemberAndBoard(Member member, Board board);
    Optional<BoardLike> findByMemberAndBoard(Member member, Board board);
    List<BoardLike> findByMemberAndBoardIn(Member member, List<Board> boards);
}