package com.jason.diarytodo.domain.cboard;

import lombok.*;

import java.sql.Timestamp;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CBoardRespDTO {
  private int boardNo;
  private String title;
  private String content;
  private String writer;
  private Timestamp postDate;
  private int viewCount;
  private int ref;
  private int step;
  private int refOrder;
  private boolean deletedFlag;
}
