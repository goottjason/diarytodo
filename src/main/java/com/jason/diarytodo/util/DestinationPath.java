package com.jason.diarytodo.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DestinationPath {

  private static String destPath;

  public static void setDestPath(HttpServletRequest req) {
    destPath = req.getRequestURI();
    log.info("destPath = " + destPath);
    req.getSession().setAttribute("destPath", destPath);
  }

}