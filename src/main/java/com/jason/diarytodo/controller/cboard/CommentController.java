package com.jason.diarytodo.controller.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.domain.common.MyResponse;
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

@RestController // @Controller + @ResponseBody
@Slf4j
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping(value = "/comment/all/{boardNo}/{pageNo}")
  public ResponseEntity<MyResponse<Map<String, Object>>> getAllComments(
    @PathVariable("boardNo") int boardNo,
    @PathVariable("pageNo") int pageNo,
    HttpSession session) {

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

    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("pageCBoardRespDTO", pageCBoardRespDTO);
    resultMap.put("loginIdByLoginMember", loginIdByLoginMember);

    return ResponseEntity.ok(MyResponse.success(resultMap));
  }


  @PostMapping(
    value = "/comment/{boardNo}",
    produces = {"application/json; charset=utf-8"})
  public ResponseEntity<MyResponse<Void>> writeComment(
    @PathVariable("boardNo") int boardNo,
    @RequestBody CommentReqDTO commentReqDTO,
    HttpSession session) {

    log.info("{}", boardNo);
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    commentReqDTO.setBoardNo(boardNo);
    commentReqDTO.setCommenter(loginMember.getLoginId());

    int result = commentService.writeComment(commentReqDTO);
    return ResponseEntity.ok(MyResponse.success());
  }

  @PatchMapping(value="/comment/{commentId}")
  public ResponseEntity<MyResponse<Void>> editComment(
    @PathVariable("commentId") Integer commentId,
    @RequestBody CommentReqDTO commentReqDTO,
    HttpSession session) {

    log.info("댓글 수정 요청: commentNo={}, commentReqDTO={}", commentId, commentReqDTO);

    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    // 1. 로그인하지 않은 경우
    if (loginMember == null) {
      return ResponseEntity.ok(MyResponse.fail(401, "NOT_LOGGED_IN"));
    }
    // 2. 댓글 정보 조회
    CommentRespDTO commentRespDTO = commentService.getCommentByCommentId(commentId);

    // 3-1. 댓글이 존재하지 않는 경우
    if (commentRespDTO == null) {
      return ResponseEntity.ok(MyResponse.fail(404, "NOT_EXIST"));
    }
    // 3-2. 로그인된 자와 댓글작성자가 불일치할 경우
    if(!loginMember.getLoginId().equals(commentRespDTO.getCommenter())) {
      return ResponseEntity.ok(MyResponse.fail(403, "FORBIDDEN"));
    }

    // 4. 댓글 수정 서비스 호출
    try {
      commentReqDTO.setCommentId(commentId);
      commentService.editComment(commentReqDTO);
      return ResponseEntity.ok(MyResponse.success());
    } catch (Exception e) {
      return ResponseEntity.ok(MyResponse.fail(500, e.getMessage()));
    }
  }

  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<MyResponse<Void>> removeComment(@PathVariable("commentId") Integer commentId, HttpSession session) {
    log.info("댓글 삭제 요청: commentId={}", commentId);
    int result = commentService.removeComment(commentId);
    if (result == 1) {
      return ResponseEntity.ok(MyResponse.success());
    } else {
      return ResponseEntity.ok(MyResponse.fail(500));
    }
  }
}
