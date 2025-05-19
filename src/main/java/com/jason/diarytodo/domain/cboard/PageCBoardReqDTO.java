package com.jason.diarytodo.domain.cboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PageCBoardReqDTO {

  @Builder.Default
  @Min(value = 1)
  private int pageNo = 1;

  @Builder.Default
  @Min(value = 10)
  @Max(value = 100)
  private int pageSize = 15;

  private int offset;

  private int boardNo;
  private int ref;
  private int step;
  private int refOrder;

  private String type;
  private String keyword;

  @Valid
  private List<CBoardReqDTO> hBoardReqDTOS;

  public int getOffset() {
    // ex. 3페이지의 offset (3 - 1) x 15 = 30
    return (pageNo - 1) * pageSize;
  }

  public List<String> getTypes() {
    if (type == null || type.isEmpty()) {
      return null;
    } else {
      return Arrays.asList(type.split("")); // of는 뭐지?
    }
  }


  public String getPageParams() {
    return generatePageParams();
  }

  public String getSearchParams() {
    return generateSearchParams();
  }

  private String generatePageParams() {

    /*
    String은 불변 객체라서 문자열을 합치거나 수정할 때마다 새로운 객체가 생성
    StringBuilder는 한 번 객체를 만들면 내부 버퍼에서 문자열을 계속 수정
      - append(str) : 문자열을 끝에 추가
      - toString() : StringBuilder 내용을 String으로 변환
     */

    StringBuilder params = new StringBuilder();
    params.append("pageNo=").append(pageNo).append("&");
    params.append("pageSize=").append(pageSize);
    return params.toString();
  }

  private String generateSearchParams() {
    StringBuilder params = new StringBuilder();
    if(type != null && !type.isBlank()) {
      params.append("type=").append(type).append("&");
    }
    if(keyword != null && !keyword.isBlank()) {
      params.append("keyword=").append(keyword);
    }
    return params.toString();
  }



}
