package com.jason.diarytodo.domain.todo;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TodoSearchReqDTO {
  @Builder.Default
  @Min(value = 1)
  private int pageNo = 1;            // 페이지 번호 (1부터)

  @Builder.Default
  @Min(value = 10)
  private int pageSize = 10;            // 페이지 크기

  private int offset;

  private String sortBy;           // 정렬 컬럼명 (ex: "duedate", "priority")
  private String sortDirection;    // "asc" or "desc"

  /*public String getTitle() {
    if(keyword != null) {
      return keyword;
    } else {
      return title;
    }
  }*/

  public int getOffset() {
    // ex. 3페이지의 offset (3 - 1) x 15 = 30
    log.info("pageNo : {}", pageNo);
    log.info("getOffset{}: ", (pageNo - 1) * pageSize);
    return (pageNo - 1) * pageSize;
  }

  /*
  allList -> X
  todayList -> duedate = LocalDate.now()
  unfinishedList -> isFinished = false
  importantList -> isImportant = true
  hasDuedateList -> hasDuedate = true -> duedate is not null
  noDuedateList -> hasDuedate = false -> duedate is null
  calPickDuedateList -> duedate = duedate;
   */
  private String status;

  // 검색/필터 필드 (MySQL 컬럼명과 일치)
  private String title;            // 제목(부분일치)
  private String content;          // 내용(부분일치)
  private String location;          // 내용(부분일치)
  private String writer;           // 작성자(정확히)
  private Boolean isFinished;      // 완료여부
  private Boolean isImportant;     // 중요여부
  private Boolean hasDuedate;
  private LocalDate duedate;   // 마감일
  private LocalDateTime createdAt; // 생성일
  private LocalDateTime updatedAt; // 수정일

  private String keyword;          // 통합 검색어 (제목+내용 등)
  private String searchType;       // 검색종류
}
