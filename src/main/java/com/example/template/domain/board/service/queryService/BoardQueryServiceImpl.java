package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.SortType;
import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryServiceImpl implements BoardQueryService {

    private final BoardRepository boardRepository;

    @Override
    public BoardResponseDTO.BoardListDTO getBoardList(Category category,
                                                      Long cursor,
                                                      Integer limit,
                                                      SortType sortType,
                                                      Member member) {
        // 첫 페이지 로딩 시 매우 큰 ID 값 사용
        if (cursor == 0) {
            cursor = Long.MAX_VALUE;
        }

        List<Board> boards;
        // 전체 조회
        if (category == null) {
            boards = sortType == SortType.LIKES
                    ? boardRepository.findAllOrderByLikesWithCursor(cursor, limit)
                    : boardRepository.findAllOrderByLatestWithCursor(cursor, limit);
        }
        // 카테고리별 조회
        else {
            boards = sortType == SortType.LIKES
                    ? boardRepository.findByCategoryOrderByLikesWithCursor(category, cursor, limit)
                    : boardRepository.findByCategoryOrderByLatestWithCursor(category, cursor, limit);
        }

        Long nextCursor = boards.isEmpty() ? null : boards.get(boards.size() - 1).getId();
        boolean hasNext = boards.size() == limit;

        return BoardResponseDTO.BoardListDTO.of(boards, nextCursor, hasNext, member.getId());
    }

    @Override
    public BoardResponseDTO.BoardDetailDTO getBoardDetail(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        return BoardResponseDTO.BoardDetailDTO.of(board, member);
    }
}
