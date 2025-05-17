package com.jason.diarytodo.domain.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRespDTO {
  private Long dno;            // 할일 번호 (PK)
  private String title;        // 제목
  private String content;      // 내용
  private String location;     // 위치
  private String writer;       // 작성자
  private Boolean isFinished;  // 완료 여부
  private Boolean isImportant; // 중요 여부
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDate duedate; // 마감일
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt; // 생성일시
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt; // 수정일시
}
