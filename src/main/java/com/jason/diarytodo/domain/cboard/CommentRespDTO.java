package com.jason.diarytodo.domain.cboard;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentRespDTO {
  private Integer commentId;        // 댓글 PK
  private Integer boardNo;          // 게시글 번호
  private String commenter;         // 작성자
  private String content;           // 내용
  private LocalDateTime createdAt;  // 작성일
  private LocalDateTime updatedAt;  // 수정일
  private Boolean deletedFlag;      // 삭제 여부
}
