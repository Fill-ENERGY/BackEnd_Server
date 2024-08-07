package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.SortType;

// BoardQueryService 인터페이스
public interface BoardQueryService {
    BoardResponseDTO.BoardListDTO getBoardList(Category category, Long cursor, Integer limit, SortType sortType);

    BoardResponseDTO.BoardDetailDTO getBoardDetail(Long boardId);
}