package com.jason.diarytodo.domain.cboard;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageCBoardRespDTO<T> {

  private int pageNo;
  private int pageSize;
  private int totalPosts;

  private int blockStartPage;
  private int blockEndPage;
  private int lastPage;

  private boolean showPrevBlockButton;
  private boolean showNextBlockButton;

  public List<T> respDTOS;


  @Builder(builderMethodName = "withPageInfo") // 빌더의 이름 지정
  public PageCBoardRespDTO(PageCBoardReqDTO pageHBoardReqDTO, List<T> respDTOS, int totalPosts) {
    this.totalPosts = totalPosts;

    this.pageNo = pageHBoardReqDTO.getPageNo();
    this.pageSize = pageHBoardReqDTO.getPageSize();

    this.blockEndPage = (((this.pageNo - 1) / this.pageSize) + 1) * this.pageSize;
    this.blockStartPage = this.blockEndPage - (this.pageSize - 1);
    this.lastPage = (int)(Math.ceil(this.totalPosts/(double)pageSize));

    this.blockEndPage = Math.min(this.blockEndPage, this.lastPage);

    if(this.blockEndPage == 0) this.blockEndPage = 1;

    this.showPrevBlockButton = this.blockStartPage > 1;
    this.showNextBlockButton = this.blockEndPage < this.lastPage;

    this.respDTOS = respDTOS;
  }
}
