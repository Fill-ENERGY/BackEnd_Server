package com.example.template.domain.board.dto.response;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponseDTO {

    @Getter
    @Builder
    public static class BoardImgDTO {
        private List<String> images;
    }


        @Getter
    @Builder
    public static class BoardDTO {
        private Long id;
        private Long memberId;
        private String memberName;
        private String title;
        private String content;
        private Category category;
        private HelpStatus helpStatus;
        private Boolean isAuthor;
        private Integer likeNum;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> images;

        public static BoardDTO from(Board board, Long currentMemberId) {
            return BoardDTO.builder()
                    .id(board.getId())
                    .memberId(board.getMember().getId())
                    .memberName(board.getMember().getName())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .category(board.getCategory())
                    .helpStatus(board.getHelpStatus())
                    .isAuthor(board.getMember().getId().equals(currentMemberId))
                    .likeNum(board.getLikeNum())
                    .commentCount(board.getCommentCount())
                    .images(board.getImages().stream().map(BoardImg::getBoardImgUrl).toList())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BoardListDTO {
        private List<BoardDTO> boards;
        private Long nextCursor;
        private boolean hasNext;

        public static BoardListDTO of(List<Board> boards, Long nextCursor, boolean hasNext, Long currentMemberId) {
            List<BoardDTO> boardDTOs = boards.stream()
                    .map(board -> BoardDTO.from(board, currentMemberId))
                    .toList();

            return BoardListDTO.builder()
                    .boards(boardDTOs)
                    .nextCursor(nextCursor)
                    .hasNext(hasNext)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BoardDetailDTO {
        private BoardDTO board;
        private List<CommentResponseDTO.CommentDTO> comments;

        public static BoardDetailDTO of(Board board, List<Comment> comments, Long currentMemberId) {
            return BoardDetailDTO.builder()
                    .board(BoardDTO.from(board, currentMemberId))
                    .comments(comments.stream().map(CommentResponseDTO.CommentDTO::from).toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BoardStatusDTO {
        private Long boardId;
        private String helpStatus;

        public static BoardStatusDTO from(Board board) {
            return BoardStatusDTO.builder()
                    .boardId(board.getId())
                    .helpStatus(board.getHelpStatus().name())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BoardLikeDTO {
        private Long memberId;
        private Long boardId;
        private Integer likeCount;

        public static BoardLikeDTO from(Board board, Long currentMemberId) {
            return BoardLikeDTO.builder()
                    .memberId(currentMemberId)
                    .boardId(board.getId())
                    .likeCount(board.getLikeNum())
                    .build();
        }
    }
}