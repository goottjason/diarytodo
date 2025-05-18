package com.jason.diarytodo.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DestinationPath {


  public static void setDestPath(HttpServletRequest request) {
    // 현재 요청 URI + 쿼리스트링 (예: /cboard/list?page=2)
    String uri = request.getRequestURI();
    String query = request.getQueryString();
    String destPath = (query == null) ? uri : uri + "?" + query;
    request.getSession().setAttribute("destPath", destPath);
  }

}