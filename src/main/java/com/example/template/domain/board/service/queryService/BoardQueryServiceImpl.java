package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.Board;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.SortType;
import com.example.template.domain.board.exception.BoardErrorCode;
import com.example.template.domain.board.exception.BoardException;
import com.example.template.domain.board.repository.BoardLikeRepository;
import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryServiceImpl implements BoardQueryService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Override
    public BoardResponseDTO.BoardListDTO getBoardList(Category category,
                                                      Long cursor,
                                                      Integer limit,
                                                      SortType sortType,
                                                      Member member) {
        cursor = initializeCursor(cursor);
        List<Board> boards = fetchBoards(category, cursor, limit, sortType);
        return createBoardListDTO(boards, limit, member);
    }

    private Long initializeCursor(Long cursor) {
        return (cursor == null || cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    @Override
    public BoardResponseDTO.BoardDTO getBoardDetail(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        boolean isLiked = boardLikeRepository.existsByMemberAndBoard(member, board);
        return BoardResponseDTO.BoardDTO.from(board, member.getId(), isLiked);
    }

    public BoardResponseDTO.BoardListDTO getMyPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member);
    }

    public BoardResponseDTO.BoardListDTO getMyCommentedPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyCommentedPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member);
    }

    public BoardResponseDTO.BoardListDTO getMyLikedPosts(Long cursor, Integer limit, Member member) {
        cursor = initializeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Board> boards = boardRepository.findMyLikedPosts(member.getId(), cursor, pageRequest);
        return createBoardListDTO(boards, limit, member);
    }

    private List<Board> fetchBoards(Category category, Long cursor, Integer limit, SortType sortType) {
        Pageable pageable = PageRequest.of(0, limit + 1); // 커서 페이지네이션을 사용하는 대신 기본적으로 `limit + 1`로 설정

        if (category == null) {
            if (sortType == SortType.LIKES) {
                return (cursor == Long.MAX_VALUE)
                        ? boardRepository.findAllOrderByLikesFirstPage(pageable) // 첫 페이지 조회
                        : boardRepository.findAllOrderByLikesWithCursor(cursor, limit + 1); // 이후 페이지 조회
            } else {
                return boardRepository.findAllOrderByLatestWithCursor(cursor, pageable);
            }
        } else {
            if (sortType == SortType.LIKES) {
                return (cursor == Long.MAX_VALUE)
                        ? boardRepository.findByCategoryOrderByLikesFirstPage(category, pageable) // 카테고리별 첫 페이지 조회
                        : boardRepository.findByCategoryOrderByLikesWithCursor(category, cursor, limit + 1); // 이후 페이지 조회
            } else {
                // 카테고리별 최신순 첫 페이지 조회
                return boardRepository.findByCategoryOrderByLatestWithCursor(category, cursor, pageable); // 이후 페이지 조회
            }
        }
    }

    private BoardResponseDTO.BoardListDTO createBoardListDTO(List<Board> boards, Integer limit, Member member) {
        boolean hasNext = boards.size() > limit;
        if (hasNext) {
            boards = boards.subList(0, limit);
        }
        Long nextCursor = hasNext ? boards.get(boards.size() - 1).getId() : null;

        // 좋아요 상태 일괄 조회
        Set<Long> likedBoardIds = boardLikeRepository.findByMemberAndBoardIn(member, boards)
                .stream()
                .map(boardLike -> boardLike.getBoard().getId())
                .collect(Collectors.toSet());

        return BoardResponseDTO.BoardListDTO.of(boards, nextCursor, hasNext, member.getId(), likedBoardIds);
    }
}
