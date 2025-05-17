package com.jason.diarytodo.domain.todo;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoReqDTO {
  @NotBlank(message = "제목은 필수 입력 항목입니다.")
  @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
  private String title;

  private String content;

  @Size(max = 200, message = "위치 정보는 200자 이내로 입력해주세요.")
  private String location;

  @NotBlank(message = "작성자는 필수 입력 항목입니다.")
  @Size(max = 100, message = "작성자명은 100자 이내로 입력해주세요.")
  private String writer;

  private Boolean isFinished;  // 완료 여부 (null 허용)

  private Boolean isImportant; // 중요 여부 (null 허용)

  @FutureOrPresent(message = "마감일은 현재 또는 미래 날짜로 설정해주세요.")
  private LocalDateTime duedate; // 마감일 (날짜 + 시간)
}
