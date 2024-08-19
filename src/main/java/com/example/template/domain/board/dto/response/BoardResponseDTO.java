package com.example.template.domain.board.dto.response;

import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.BoardImg;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        private boolean isAuthor;
        private boolean isLiked;
        private Integer likeNum;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> images;

        public static BoardDTO from(Board board, Long currentMemberId, boolean isLiked) {
            return BoardDTO.builder()
                    .id(board.getId())
                    .memberId(board.getMember().getId())
                    .memberName(board.getMember().getName())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .category(board.getCategory())
                    .helpStatus(board.getHelpStatus())
                    .isAuthor(board.getMember().getId().equals(currentMemberId))
                    .isLiked(isLiked)
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

        public static BoardListDTO of(List<Board> boards, Long nextCursor, boolean hasNext, Long currentMemberId, Set<Long> likedBoardIds) {
            List<BoardDTO> boardDTOs = boards.stream()
                    .map(board -> BoardDTO.from(board, currentMemberId, likedBoardIds.contains(board.getId())))
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
        private boolean isLiked;
        private Integer likeCount;

        public static BoardLikeDTO from(Board board, Long currentMemberId, boolean isLiked) {
            return BoardLikeDTO.builder()
                    .memberId(currentMemberId)
                    .boardId(board.getId())
                    .isLiked(isLiked)
                    .likeCount(board.getLikeNum())
                    .build();
        }
    }
}