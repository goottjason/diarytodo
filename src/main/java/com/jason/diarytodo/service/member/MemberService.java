package com.jason.diarytodo.service.member;

import com.jason.diarytodo.domain.member.LoginDTO;
import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;

public interface MemberService {

  MemberRespDTO login(LoginDTO loginDTO);

  int registerMember(MemberReqDTO memberReqDTO);

  MemberRespDTO checkIdDuplication(String loginId);

  MemberRespDTO checkEmailUnique(String email);
}
