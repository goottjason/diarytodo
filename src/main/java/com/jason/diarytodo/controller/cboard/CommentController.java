package com.jason.diarytodo.controller.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.domain.common.MyResponseWithData;
import com.jason.diarytodo.domain.common.MyResponseWithoutData;
import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.service.cboard.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/comment/all/{boardNo}/{pageNo}")
  public ResponseEntity<MyResponseWithData> getAllComments(@PathVariable("boardNo") int boardNo, @PathVariable("pageNo") int pageNo, HttpSession session) {
    log.info("댓글형 : {}번 글의 {} 페이지 댓글 조회... ", boardNo, pageNo);

    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    String loginIdByLoginMember = (loginMember != null) ? loginMember.getLoginId() : null;

    PageCBoardReqDTO pageCBoardReqDTO = PageCBoardReqDTO.builder()
      .boardNo(boardNo)
      .pageNo(pageNo)
      .pageSize(10)
      .build();

    PageCBoardRespDTO<CommentRespDTO> pageCBoardRespDTO =
      commentService.getAllComments(pageCBoardReqDTO);

    Map<String, Object> result = new HashMap<>();
    result.put("pageCBoardRespDTO", pageCBoardRespDTO);
    result.put("loginIdByLoginMember", loginIdByLoginMember);
    return ResponseEntity.ok(MyResponseWithData.success(result));
  }

  @PostMapping(value = "/comment/{boardNo}", produces = {"application/json; charset=utf-8"})
  public ResponseEntity<MyResponseWithData> writeComment(@PathVariable("boardNo") int boardNo, @RequestBody CommentReqDTO commentReqDTO, HttpSession session) {
    log.info("{}", boardNo);
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    commentReqDTO.setBoardNo(boardNo);
    commentReqDTO.setCommenter(loginMember.getLoginId());

    int result = commentService.writeComment(commentReqDTO);
    return ResponseEntity.ok(MyResponseWithData.success());
  }

  @PatchMapping(value="/{commentNo}")
  public ResponseEntity<MyResponseWithoutData> editComment(@PathVariable("commentNo") Integer commentNo, @RequestBody CommentReqDTO commentReqDTO, HttpSession session) {
    log.info("댓글 수정 요청: commentNo={}, commentDTO={}", commentNo, commentReqDTO);
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");
    if (loginMember == null) {
      return ResponseEntity.ok(new MyResponseWithoutData(401, null, "NOT_LOGGED_IN"));
    }
    CommentRespDTO commentRespDTO = commentService.getCommentByCommentNo(commentNo);

    if (commentRespDTO == null) {
      return ResponseEntity.ok(new MyResponseWithoutData(404, null, "NOT_EXIST"));
    }
    // 로그인된 자와 댓글작성자가 불일치할 경우, FORBIDDEN 반환
    if(!loginMember.getLoginId().equals(commentRespDTO.getCommenter())) {
      return ResponseEntity.ok(new MyResponseWithoutData(403, null, "FORBIDDEN"));
    }
    try {
      commentReqDTO.setCommentId(commentNo);
      commentService.editComment(commentReqDTO);
      return ResponseEntity.ok(new MyResponseWithoutData(200, null, "SUCCESS"));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(new MyResponseWithoutData(400, null, "FAIL"));
    }
  }



}
