package com.jason.diarytodo.controller.member;
import com.jason.diarytodo.domain.member.LoginDTO;
import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.service.member.MemberService;
import com.jason.diarytodo.util.EmailSender;
import jakarta.mail.MessagingException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final EmailSender emailSender;


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
  public String signup(Model model) {
    model.addAttribute("memberReqDTO", new MemberReqDTO());
    return "member/signup";
  }
  @PostMapping("/member/signup")
  public String signup(@Valid @ModelAttribute MemberReqDTO memberReqDTO,
                       BindingResult bindingResult,
                       @RequestParam("passwordConfirm") String passwordConfirm,
                       RedirectAttributes redirectAttributes,
                       Model model) {

    // 1. 비밀번호 두번 입력 일치 검사 -> 불일치할 때 bindingResult에 오류를 추가
    if(!memberReqDTO.getPassword().equals(passwordConfirm)) {
      bindingResult.rejectValue("password", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
    }

    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
      }
      log.info("errors: {}", errors);
      model.addAttribute("errors", errors);
      redirectAttributes.addFlashAttribute("signupValidFailMsg", errors);
      return "/member/signup";
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
  public ResponseEntity<Map<String, String>> checkId(@Valid @RequestBody MemberReqDTO memberReqDTO,
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

  @PostMapping("/member/checkEmail")
  public ResponseEntity<Map<String, String>> checkEmail(@Valid @RequestBody MemberReqDTO memberReqDTO,
                                                        BindingResult bindingResult) {
    log.info("[[Controller]] {}", memberReqDTO);
    if (bindingResult.hasFieldErrors("email")) {
      String errorMsg = bindingResult.getFieldError("email").getDefaultMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        Collections.singletonMap("emailStatus", errorMsg));
    }
    MemberRespDTO memberRespDTO = memberService.checkEmailUnique(memberReqDTO.getEmail());
    if (memberRespDTO == null) {
      return ResponseEntity.ok(Collections.singletonMap("emailStatus", "사용가능한 이메일입니다."));
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        Collections.singletonMap("emailStatus", "중복된 이메일이 있습니다."));
    }
  }

  @PostMapping("/member/callSendEmail")
  public ResponseEntity<String> callSendEmail(@RequestBody MemberReqDTO memberReqDTO,
                                              HttpSession session) throws MessagingException {
    SecureRandom random = new SecureRandom();
    int randomNumber = random.nextInt(1000000); // 0 ~ 999999
    String authCode = String.format("%06d", randomNumber);

    // 이메일을 보낸 후, 세션에 저장 (사용자가 인증코드 입력하고 제출할 때 꺼내서 입력값과 비교)
    emailSender.sendAuthCodeEmail(memberReqDTO.getEmail(), authCode);
    session.setAttribute("authCode", authCode);
    return new ResponseEntity<>("success", HttpStatus.OK);
  }

  @PostMapping("/member/checkAuthCode")
  public ResponseEntity<String> checkAuthCode(@RequestParam("userAuthCode") String userAuthCode, HttpSession session) {
    if(session.getAttribute("authCode") != null) {
      String sessionAuthCode = (String) session.getAttribute("authCode");
      if(userAuthCode.equals(sessionAuthCode)) {
        return new ResponseEntity<>("success", HttpStatus.OK);
      }
    }
    return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
  }
}
