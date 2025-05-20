package com.jason.diarytodo.domain.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TodoSearchRespDTO {

  private int pageNo;
  private int pageSize;
  private int totalTodos;

  private int blockStartPage;
  private int blockEndPage;
  private int lastPage;

  private boolean showPrevBlockButton;
  private boolean showNextBlockButton;

  private List<TodoRespDTO> todos;

  @Builder(builderMethodName = "withPageInfo")
  public TodoSearchRespDTO(int totalTodos, TodoSearchReqDTO todoSearchReqDTO, List<TodoRespDTO> todos) {
    this.totalTodos = totalTodos;

    this.pageNo = todoSearchReqDTO.getPageNo();
    this.pageSize = todoSearchReqDTO.getPageSize();
    this.todos = todos;

    this.lastPage = (int)(Math.ceil(this.totalTodos/(double)pageSize));

    calculateBlockPages();

    this.showPrevBlockButton = this.blockStartPage > 1;
    this.showNextBlockButton = this.blockEndPage < this.lastPage;
  }

  private void calculateBlockPages() {
    // 해당 블록의 마지막 페이지 번호 계산
    int initialBlockEnd = ((this.pageNo - 1) / this.pageSize + 1) * this.pageSize;
    // 전체 마지막 페이지를 넘지 않도록 보정, 0 이하로 내려가지 않도록 1로 보정
    this.blockEndPage = Math.max(Math.min(initialBlockEnd, this.lastPage), 1);
    // 해당 블록의 시작 페이지 번호 계산, 1 미만으로 내려가지 않도록 1로 보정
    this.blockStartPage = Math.max(this.blockEndPage - (this.pageSize - 1), 1);
  }
}
