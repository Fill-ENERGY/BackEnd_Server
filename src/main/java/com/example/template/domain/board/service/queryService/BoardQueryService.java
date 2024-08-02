package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.BoardResponseDTO;

// BoardQueryService 인터페이스
public interface BoardQueryService {
    BoardResponseDTO.BoardDetailDTO getBoardDetail(Long boardId);
}