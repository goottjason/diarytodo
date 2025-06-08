package com.jason.diarytodo.domain.cboard;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CBoardRespDTO {
  private int boardNo;            // 글 번호 (board_no)
  private String title;           // 제목 (title)
  private String content;         // 내용 (content)
  private String writer;          // 작성자 (writer)
  private int viewCount;          // 조회수 (view_count)
  private Integer commentCount;   // 댓글수 (comment_count)
  private int ref;                // 참조글 번호 (ref)
  private int step;               // 들여쓰기 단계 (step)
  private int refOrder;           // 참조글 내 순서 (ref_order)
  private LocalDateTime createdAt;    // 생성일시 (created_at)
  private LocalDateTime updatedAt;    // 수정일시 (updated_at)
  private LocalDateTime deletedAt;    // 삭제일시 (deleted_at)
}
