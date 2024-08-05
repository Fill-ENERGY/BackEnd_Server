package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.HelpStatus;

public interface BoardCommandService {
    BoardResponseDTO.BoardDTO createBoard(BoardRequestDTO.CreateBoardDTO createBoardDTO);
    BoardResponseDTO.BoardDTO updateBoard(Long boardId, BoardRequestDTO.UpdateBoardDTO updateBoardDTO);
    Long deleteBoard(Long boardId);
    BoardResponseDTO.BoardStatusDTO updateBoardStatus(Long boardId, BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO);
    BoardResponseDTO.BoardLikeDTO addLike(Long boardId);
    BoardResponseDTO.BoardLikeDTO removeLike(Long boardId);
}