package com.jason.diarytodo.domain.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatusCountDTO {
  private int todayListCnt;
  private int unfinishedListCnt;
  private int importantListCnt;
  private int hasDuedateListCnt;
  private int noDuedateListCnt;
  private int calPickDuedateListCnt;
  private int allListCnt;
}
