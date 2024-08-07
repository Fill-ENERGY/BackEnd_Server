package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.HelpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardCommandService {
    BoardResponseDTO.BoardImgDTO uploadImages(List<MultipartFile> images);
    BoardResponseDTO.BoardDTO createBoard(BoardRequestDTO.CreateBoardDTO createBoardDTO);
    BoardResponseDTO.BoardDTO updateBoard(Long boardId, BoardRequestDTO.UpdateBoardDTO updateBoardDTO);
    Long deleteBoard(Long boardId);
    BoardResponseDTO.BoardStatusDTO updateBoardStatus(Long boardId, BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO);
    BoardResponseDTO.BoardLikeDTO addLike(Long boardId);
    BoardResponseDTO.BoardLikeDTO removeLike(Long boardId);
}