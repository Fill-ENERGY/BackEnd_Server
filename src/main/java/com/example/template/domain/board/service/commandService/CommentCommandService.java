package com.example.template.domain.board.service.commandService;

import com.example.template.domain.board.dto.request.CommentRequestDTO;
import com.example.template.domain.board.dto.response.CommentResponseDTO;
import com.example.template.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentCommandService {
    CommentResponseDTO.CommentImgDTO uploadCommentImages(List<MultipartFile> images);
    CommentResponseDTO.CommentDTO createComment(Long boardId, CommentRequestDTO.CreateDTO createDTO, Member member);
    CommentResponseDTO.CommentDTO updateComment(Long boardId, Long commentId, CommentRequestDTO.UpdateDTO updateDTO, Member member);
    Long deleteComment(Long boardId, Long commentId, Member member);
}
