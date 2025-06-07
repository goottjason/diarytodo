package com.jason.diarytodo.interceptor;

import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.service.member.MemberService;
import com.jason.diarytodo.util.DestinationPath;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor // 스프링컨트롤러에서 주는 빈을 쓰려면...
public class AuthInterceptor implements HandlerInterceptor {

  private final MemberService memberService;

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    // log.info("====================================== Auth ◆ preHandle() 호출");

    HttpSession session = request.getSession();

    // 이미 로그인된 경우, 바로 return
    if(session.getAttribute("loginMember") != null) {
      return true;
    }

    // 자동 로그인 쿠키 확인
    Cookie[] cookies = request.getCookies();
    String autoLoginSessionId = null;
    // 쿠키가 하나라도 있으면 하나씩 순회
    if(cookies != null) {
      for(Cookie cookie : cookies) {
        if("autoLoginSessionId".equals(cookie.getName())) {
          autoLoginSessionId = cookie.getValue();
          break;
        }
      }
    }

    // 사용자가 자동로그인 선택하여, 저장한 쿠키가 있으면,
    if(autoLoginSessionId != null) {
      // DB에서 세션 ID 검증
      MemberRespDTO member = memberService.getMemberByAutoLoginSessionId(autoLoginSessionId);

      if(member != null && member.getAutoLoginLimit().isAfter(LocalDateTime.now())) {
        // 세션에 로그인 정보 저장
        session.setAttribute("loginMember", member);

        // 자동 로그인 갱신 (옵션)
        memberService.updateAutoLoginInfo(member.getLoginId(), autoLoginSessionId,LocalDateTime.now().plusDays(180));
        return true;
      } else {
        // 유효하지 않은 쿠키 삭제
        Cookie invalidCookie = new Cookie("autoLogin", null);
        invalidCookie.setMaxAge(0);
        invalidCookie.setPath("/");
        response.addCookie(invalidCookie);
      }
    }

    // 로그인 페이지 리다이렉트
    DestinationPath.setDestPath(request);
    response.sendRedirect("/member/login");
    return false;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    // log.info("====================================== Auth ◆ postHandle() 호출");

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    // log.info("====================================== Auth ◆ afterCompletion() 호출");

  }
}