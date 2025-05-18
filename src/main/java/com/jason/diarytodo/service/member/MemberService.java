package com.jason.diarytodo.service.member;

import com.jason.diarytodo.domain.member.LoginReqDTO;
import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;

import java.time.LocalDateTime;

public interface MemberService {

  MemberRespDTO login(LoginReqDTO loginDTO);

  int registerMember(MemberReqDTO memberReqDTO);

  MemberRespDTO checkIdDuplication(String loginId);

  MemberRespDTO checkEmailUnique(String email);

  void clearAuthLoginInfo(String loginId);

  void updateAutoLoginInfo(String loginId, String autoLoginSessionId, LocalDateTime autoLoginLimit);

  MemberRespDTO getMemberByAutoLoginSessionId(String autoLoginSessionId);
}
