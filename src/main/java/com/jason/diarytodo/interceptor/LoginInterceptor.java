package com.jason.diarytodo.interceptor;

import com.jason.diarytodo.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

  // mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request, response를 가로채서 동작
  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    boolean result = false;

    log.info("====================================== preHandle() 호출");

    if (handler instanceof HandlerMethod) {
      HandlerMethod handlerMethod = (HandlerMethod) handler;

      // 메서드 이름
      Method method = handlerMethod.getMethod();
      log.info("===== method : {}", method);

      // 클래스 이름
      String controllerName = handlerMethod.getBeanType().getName(); // method.getDeclaringClass().getName();
      log.info("===== controllerName : {}", controllerName);
    }

    String uri = request.getRequestURI();
    String method = request.getMethod();
    log.info("===== uri : {}", uri);
    log.info("===== method : {}", method);
    if (uri.equals("/member/login") && method.equalsIgnoreCase("GET")) {
      // GET 방식 : /member/login 요청은 통과시켜야 한다.
      result = true;
    }
    if (uri.equals("/member/login") && method.equalsIgnoreCase("POST")) {
      // POST 방식 :
      result = true;
    }

    return result; // false: 해당 컨트롤러단의 메서드로 제어가 돌아가지 않는다.

  }

  // mapping되는 컨트롤러단의 메서드가 호출되어 실행된 후에, request와 response를 가로채서 동작
  @Override
  public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) throws Exception {

    log.info("====================================== postHandle() 호출");

    if (request.getMethod().equalsIgnoreCase("POST")) {
      Map<String, Object> model = modelAndView.getModel();
      Member loginMember = (Member) model.get("loginMember");
      log.info("loginMember : {}", loginMember);

      if (loginMember != null) {
        // 로그인 성공 -> 로그인 멤버정보를 세션에 저장 -> "/"으로
        log.info("postHandle() ... 로그인 성공 ... loginMember : {}", loginMember);
        HttpSession ses = request.getSession();
        ses.setAttribute("loginMember", loginMember);
        String destPath = (String) ses.getAttribute("destPath");
        if (destPath != null) {
          response.sendRedirect(destPath);
        } else {
          response.sendRedirect("/");
        }

      } else {
        // 로그인 실패
        log.info("postHandle() ... 로그인 실패 ...");

        response.sendRedirect("/member/login?status=fail");
      }
    }

  }

  // 해당 인터셉터의 prehandle, posthandle의 전 과정이 끝난 후 view가 렌더링 된 후에 request와 response를 가로채서 동작함
  @Override
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
    log.info("====================================== afterCompletion() 호출");
    // HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}