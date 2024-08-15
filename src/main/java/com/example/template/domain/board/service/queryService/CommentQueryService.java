package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.CommentResponseDTO;
import com.example.template.domain.member.entity.Member;

public interface CommentQueryService {
    public CommentResponseDTO.CommentsListDTO getCommentsList(Long boardId, Member member);
}
