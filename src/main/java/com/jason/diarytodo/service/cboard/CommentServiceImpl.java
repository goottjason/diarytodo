package com.jason.diarytodo.service.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.mapper.cboard.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final CommentMapper commentMapper;

  @Override
  public PageCBoardRespDTO<CommentRespDTO> getAllComments(PageCBoardReqDTO pageCBoardReqDTO) {
    List<CommentRespDTO> commentRespDTOS = commentMapper.selectAllCommentsLimitPage(pageCBoardReqDTO);
    int total = commentMapper.selectCommentCount(pageCBoardReqDTO.getBoardNo());

    return PageCBoardRespDTO.<CommentRespDTO>withPageInfo()
      .pageHBoardReqDTO(pageCBoardReqDTO)
      .totalPosts(total)
      .respDTOS(commentRespDTOS)
      .build();
  }

  @Override
  public int writeComment(CommentReqDTO commentReqDTO) {
    return commentMapper.insertComment(commentReqDTO);
  }

  @Override
  public CommentRespDTO getCommentByCommentNo(Integer commentNo) {
    return commentMapper.selectCommentByCommentNo(commentNo);
  }

  @Override
  public int editComment(CommentReqDTO commentReqDTO) {
    return commentMapper.updateComment(commentReqDTO);
  }
}
