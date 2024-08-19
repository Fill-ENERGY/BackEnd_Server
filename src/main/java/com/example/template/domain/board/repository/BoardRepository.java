package com.example.template.domain.board.repository;


import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.enums.Category;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE b.id < :cursor ORDER BY b.likeNum DESC, b.id DESC")
    List<Board> findAllOrderByLikesWithCursor(@Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.id < :cursor ORDER BY b.id DESC")
    List<Board> findAllOrderByLatestWithCursor(@Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.category = :category AND b.id < :cursor ORDER BY b.likeNum DESC, b.id DESC")
    List<Board> findByCategoryOrderByLikesWithCursor(@Param("category") Category category, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.category = :category AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findByCategoryOrderByLatestWithCursor(@Param("category") Category category, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Board b JOIN b.comments c WHERE c.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyCommentedPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN BoardLike bl ON b.id = bl.board.id WHERE bl.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyLikedPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);
}

