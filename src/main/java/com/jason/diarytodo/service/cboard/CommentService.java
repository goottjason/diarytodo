package com.jason.diarytodo.service.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;

public interface CommentService {
  PageCBoardRespDTO<CommentRespDTO> getAllComments(PageCBoardReqDTO pageCBoardReqDTO);

  int writeComment(CommentReqDTO commentReqDTO);

  CommentRespDTO getCommentByCommentNo(Integer commentNo);

  int editComment(CommentReqDTO commentReqDTO);
}
