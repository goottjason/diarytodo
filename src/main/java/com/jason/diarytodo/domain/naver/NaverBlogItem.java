package com.jason.diarytodo.domain.naver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverBlogItem {
  private String title;
  private String link;
  private String description;
  private String bloggername;
  private String bloggerlink;
  private String postdate;
}
