package com.jason.diarytodo.controller.cboard;

import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.domain.common.MyResponseWithData;
import com.jason.diarytodo.service.cboard.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/comment/all/{boardNo}/{pageNo}")
  public ResponseEntity<MyResponseWithData> getAllComments(@PathVariable int boardNo, @PathVariable int pageNo) {
    log.info("댓글형 : {}번 글의 {} 페이지 댓글 조회... ", boardNo, pageNo);

    PageCBoardReqDTO pageCBoardReqDTO = PageCBoardReqDTO.builder()
      .boardNo(boardNo)
      .pageNo(pageNo)
      .pageSize(10)
      .build();

    PageCBoardRespDTO<CommentRespDTO> pageCBoardRespDTO =
      commentService.getAllComments(pageCBoardReqDTO);
    return ResponseEntity.ok(MyResponseWithData.success(pageCBoardRespDTO));
  }
}
