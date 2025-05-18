package com.jason.diarytodo.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetClientIpAddr {
  public static String getClientIp(HttpServletRequest request) {

    String ip = request.getHeader("X-Forwarded-For");

    if (ip == null) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    if (ip == null) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ip == null) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }

    if (ip == null) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (ip == null) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }
}
