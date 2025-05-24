package com.jason.diarytodo.domain.cboard;

import com.jason.diarytodo.domain.common.AttachmentReqDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CBoardReqDTO {

  private int boardNo;

  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 50, message = "제목은 50자 이내로 작성해 주세요")
  private String title;

  @Size(max = 500, message = "내용은 2000자 이내로 작성해 주세요")
  private String content;

  private String writer;

  private int ref;
  private int step;
  private int refOrder;

  private List<AttachmentReqDTO> uploadfiles;
}
