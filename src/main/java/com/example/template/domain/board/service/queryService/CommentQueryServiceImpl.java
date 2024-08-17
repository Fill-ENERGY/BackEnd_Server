package com.example.template.domain.board.service.queryService;

import com.example.template.domain.board.dto.response.CommentResponseDTO;
import com.example.template.domain.board.entity.Comment;
import com.example.template.domain.board.repository.CommentRepository;
import com.example.template.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDTO.CommentsListDTO getCommentsList(Long boardId, Member member) {

        List<Comment> comments = commentRepository.findByBoardId(boardId);

        return CommentResponseDTO.CommentsListDTO.of(comments, member);
    }

}
