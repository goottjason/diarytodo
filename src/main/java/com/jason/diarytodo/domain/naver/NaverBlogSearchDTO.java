package com.jason.diarytodo.domain.naver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverBlogSearchDTO {
  private String lastBuildDate;
  private int total;
  private int start;
  private int display;
  private List<NaverBlogItem> items;
}
