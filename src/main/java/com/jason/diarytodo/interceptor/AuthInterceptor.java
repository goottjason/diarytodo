package com.jason.diarytodo.interceptor;

import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.util.DestinationPath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
@RequiredArgsConstructor // 스프링컨트롤러에서 주는 빈을 쓰려면...
public class AuthInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    log.info("====================================== Auth ◆ preHandle() 호출");
    // 목적지 저장
    DestinationPath.setDestPath(request);

    // 로그인 여부를 검사
    HttpSession ses = request.getSession();
    MemberRespDTO loginMember = (MemberRespDTO) ses.getAttribute("loginMember");

    if(loginMember == null) {
      log.info("로그인 하지 않은 사용자 -> 로그인 페이지로 이동");
      response.sendRedirect("/member/login");
      return false;
    }

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    log.info("====================================== Auth ◆ postHandle() 호출");

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    log.info("====================================== Auth ◆ afterCompletion() 호출");

  }
}