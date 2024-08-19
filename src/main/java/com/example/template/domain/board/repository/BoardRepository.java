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
    // 좋아요 순 정렬 - 전체 게시물 (첫 페이지)
    @Query("SELECT b FROM Board b ORDER BY b.likeNum DESC, b.id DESC")
    List<Board> findAllOrderByLikesFirstPage(Pageable pageable);

    // 좋아요 순 정렬 - 전체 게시물 (이후 페이지)
    @Query(value = "SELECT b1.* FROM board b1 " +
            "JOIN (SELECT b2.board_id, CONCAT(LPAD(CAST(b2.like_num AS CHAR(10)), 10, '0'), LPAD(CAST(b2.board_id AS CHAR(10)), 10, '0')) as cursorValue " +
            "      FROM board b2) as cursorTable ON cursorTable.board_id = b1.board_id " +
            "WHERE cursorValue < (SELECT CONCAT(LPAD(CAST(b3.like_num AS CHAR(10)), 10, '0'), LPAD(CAST(b3.board_id AS CHAR(10)), 10, '0')) " +
            "                                FROM board b3 WHERE b3.board_id = :cursor) " +
            "ORDER BY b1.like_num DESC, b1.board_id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Board> findAllOrderByLikesWithCursor(@Param("cursor") Long cursor, @Param("limit") int limit);

    // 좋아요 순 정렬 - 카테고리별 (첫 페이지)
    @Query("SELECT b FROM Board b WHERE b.category = :category ORDER BY b.likeNum DESC, b.id DESC")
    List<Board> findByCategoryOrderByLikesFirstPage(@Param("category") Category category, Pageable pageable);

    // 좋아요 순 정렬 - 카테고리별 (이후 페이지)
    @Query(value = "SELECT b1.* FROM board b1 " +
            "JOIN (SELECT b2.board_id, CONCAT(LPAD(CAST(b2.like_num AS CHAR(10)), 10, '0'), LPAD(CAST(b2.board_id AS CHAR(10)), 10, '0')) as cursorValue " +
            "      FROM board b2 ) as cursorTable ON cursorTable.board_id = b1.board_id " +
            "WHERE cursorValue < (SELECT CONCAT(LPAD(CAST(b3.like_num AS CHAR(10)), 10, '0'), LPAD(CAST(b3.board_id AS CHAR(10)), 10, '0')) " +
            "                                FROM board b3 WHERE b3.board_id = :cursor AND b3.category = :#{#category.name()})  " +
            "ORDER BY b1.like_num DESC, b1.board_id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Board> findByCategoryOrderByLikesWithCursor(@Param("category") Category category, @Param("cursor") Long cursor, @Param("limit") int limit);

    @Query("SELECT b FROM Board b WHERE b.id < :cursor ORDER BY b.id DESC")
    List<Board> findAllOrderByLatestWithCursor(@Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.category = :category AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findByCategoryOrderByLatestWithCursor(@Param("category") Category category, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Board b JOIN b.comments c WHERE c.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyCommentedPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN BoardLike bl ON b.id = bl.board.id WHERE bl.member.id = :memberId AND b.id < :cursor ORDER BY b.id DESC")
    List<Board> findMyLikedPosts(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);
}

