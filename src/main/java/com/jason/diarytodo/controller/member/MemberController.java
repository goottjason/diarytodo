package com.jason.diarytodo.controller.member;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberRespDTO;
import com.jason.diarytodo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/member/login")
  public String login() {
    return "member/login";
  }
  @PostMapping("/member/login")
  public String login(LoginDTO loginDTO,
                    HttpSession session,
                    RedirectAttributes redirectAttributes) {

    /* [[ 로그인흐름 ]]
    컨트롤러 도달 전에 DispatcherServlet이 요청을 받아서 LoginInterceptor의 preHandle() 실행
      -> preHandle에서 POST요청은 자동 통과
    컨트롤러 도달하여 로그인 시도
      -> void이므로 뷰 이름 명시하지 않음 (요청 URL과 동일한 이름의 뷰를 찾게 됨)
      -> 뷰를 반환하지 않고, model에만 값을 담은 뒤 실질적인 응답 처리는 Interceptor에서 담당
    postHandle() 실행
      -> model에서 loginMember를 꺼내서 성공이면, 세션에 저장하여 리다이렉트 (실패면 로그인폼으로 리다이렉트)
     */
    MemberRespDTO loginMember = memberService.login(loginDTO);

    if (loginMember != null) {
      log.info("[[Controller]] login success... loginDTO: {}", loginDTO);
      session.setAttribute("loginMember", loginMember);
      return "redirect:/";
    } else {
      log.info("[[Controller]] login failure...");
      redirectAttributes.addFlashAttribute(
        "authFailMsg",
        "아이디 또는 비밀번호가 올바르지 않습니다.");
      return "redirect:/member/login";
    }
  }

  @GetMapping("/member/signup")
  public String signup() {
    return "member/signup";
  }
  @PostMapping("/mamber/signup")
  public String signup(HttpSession session, MemberRespDTO memberRespDTO) {
    return "member/signup";
  }
}
