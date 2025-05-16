package com.jason.diarytodo.controller.member;
import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberReqDTO;
import com.jason.diarytodo.domain.MemberRespDTO;
import com.jason.diarytodo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  // ==============================================================
  // ----- 로그인
  // ==============================================================
  @GetMapping("/member/login")
  public String login() {
    return "member/login";
  }
  @PostMapping("/member/login")
  public String login(LoginDTO loginDTO,
                    HttpSession session,
                    RedirectAttributes redirectAttributes) {

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

  // ==============================================================
  // ----- 회원가입
  // ==============================================================
  @GetMapping("/member/signup")
  public String signup() {
    return "member/signup";
  }
  @PostMapping("/member/signup")
  public String signup(@Valid MemberReqDTO memberReqDTO,
                       BindingResult bindingResult,
                       @RequestParam("passwordConfirm") String passwordConfirm,
                       RedirectAttributes redirectAttributes) {

    if(!memberReqDTO.getPassword().equals(passwordConfirm)) {
      bindingResult.rejectValue("password", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
    }

    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
      }
      log.info("errors: {}", errors);
      redirectAttributes.addFlashAttribute("signupValidFailMsg", errors);
      return "redirect:/member/signup";
    }


    int result = memberService.registerMember(memberReqDTO);
    if (result == 1) {
      redirectAttributes.addFlashAttribute(
        "signupSuccessMsg",
        "회원가입을 축하드립니다! 로그인 후 서비스를 이용해주세요.");
      return "redirect:/member/login";
    } else {
      redirectAttributes.addFlashAttribute(
        "signupFailMsg",
        "회원가입에 실패하였습니다. 다시 가입을 시도해주세요.");
      return "redirect:/member/signup";
    }
  }
  @PostMapping("/member/checkId")
  public ResponseEntity<Map<String, String>> idCheck(@Valid @RequestBody MemberReqDTO memberReqDTO,
                                                     BindingResult bindingResult) {
    log.info("[[Controller]] {}", memberReqDTO);
    if (bindingResult.hasFieldErrors("loginId")) {
      String errorMsg = bindingResult.getFieldError("loginId").getDefaultMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        Collections.singletonMap("loginIdStatus", errorMsg));
    }
    MemberRespDTO memberRespDTO = memberService.checkIdDuplication(memberReqDTO.getLoginId());
    if (memberRespDTO == null) {
      return ResponseEntity.ok(Collections.singletonMap("loginIdStatus", "사용가능한 ID입니다."));
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        Collections.singletonMap("loginIdStatus", "중복된 ID가 있습니다."));
    }
  }
}
