package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.SortType;
import com.example.template.domain.member.entity.Member;

// BoardQueryService 인터페이스
public interface BoardQueryService {
    BoardResponseDTO.BoardListDTO getBoardList(Category category,
                                               Long cursor,
                                               Integer limit,
                                               SortType sortType,
                                               Member member);

    BoardResponseDTO.BoardDTO getBoardDetail(Long boardId, Member member);

    BoardResponseDTO.BoardListDTO getMyPosts(Long cursor, Integer limit, Member member);

    BoardResponseDTO.BoardListDTO getMyCommentedPosts(Long cursor, Integer limit, Member member);

    BoardResponseDTO.BoardListDTO getMyLikedPosts(Long cursor, Integer limit, Member member);
}