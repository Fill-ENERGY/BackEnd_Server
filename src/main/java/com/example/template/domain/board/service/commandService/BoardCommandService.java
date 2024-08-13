package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardCommandService {
    BoardResponseDTO.BoardImgDTO uploadBoardImages(List<MultipartFile> images);
    BoardResponseDTO.BoardDTO createBoard(BoardRequestDTO.CreateBoardDTO createBoardDTO, Member member);
    BoardResponseDTO.BoardDTO updateBoard(Long boardId, BoardRequestDTO.UpdateBoardDTO updateBoardDTO, Member member);
    Long deleteBoard(Long boardId, Member member);
    BoardResponseDTO.BoardStatusDTO updateBoardStatus(Long boardId,
                                                      BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO,
                                                      Member member);
    BoardResponseDTO.BoardLikeDTO addLike(Long boardId, Member member);
    BoardResponseDTO.BoardLikeDTO removeLike(Long boardId, Member member);
}