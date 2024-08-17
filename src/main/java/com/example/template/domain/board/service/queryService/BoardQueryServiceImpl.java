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
import org.springframework.data.domain.PageRequest;
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
        cursor = initializeCursor(cursor);
        List<Board> boards = fetchBoards(category, cursor, limit, sortType);
        return createBoardListDTO(boards, limit, member.getId());
    }

    @Override
    public BoardResponseDTO.BoardDTO getBoardDetail(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
        return BoardResponseDTO.BoardDTO.from(board, member.getId());
    }

    public BoardResponseDTO.BoardListDTO getMyPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member.getId());
    }

    public BoardResponseDTO.BoardListDTO getMyCommentedPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyCommentedPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member.getId());
    }

    public BoardResponseDTO.BoardListDTO getMyLikedPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyLikedPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member.getId());
    }

    private Long initializeCursor(Long cursor) {
        return (cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    private List<Board> fetchBoards(Category category, Long cursor, Integer limit, SortType sortType) {
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        if (category == null) {
            if (sortType == SortType.LIKES) {
                return boardRepository.findAllOrderByLikesWithCursor(cursor, pageRequest);
            } else {
                return boardRepository.findAllOrderByLatestWithCursor(cursor, pageRequest);
            }
        } else {
            if (sortType == SortType.LIKES) {
                return boardRepository.findByCategoryOrderByLikesWithCursor(category, cursor, pageRequest);
            } else {
                return boardRepository.findByCategoryOrderByLatestWithCursor(category, cursor, pageRequest);
            }
        }
    }

    private BoardResponseDTO.BoardListDTO createBoardListDTO(List<Board> boards, Integer limit, Long memberId) {
        boolean hasNext = boards.size() > limit;
        if (hasNext) {
            boards = boards.subList(0, limit);
        }
        Long nextCursor = hasNext ? boards.get(boards.size() - 1).getId() : null;
        return BoardResponseDTO.BoardListDTO.of(boards, nextCursor, hasNext, memberId);
    }
}
