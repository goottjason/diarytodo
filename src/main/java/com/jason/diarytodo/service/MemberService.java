package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberReqDTO;
import com.jason.diarytodo.domain.MemberRespDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface MemberService {

  MemberRespDTO login(LoginDTO loginDTO);

  int registerMember(MemberReqDTO memberReqDTO);

  MemberRespDTO checkIdDuplication(String loginId);

  MemberRespDTO checkEmailUnique(String email);
}
