package com.jason.diarytodo.domain.cboard;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentReqDTO {
  private int commentId;
  private int boardNo;     // 댓글이 달릴 게시글 번호
  private String commenter;    // 댓글 작성자 ID 또는 닉네임
  private String content;      // 댓글 내용
}
