package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberReqDTO;
import com.jason.diarytodo.domain.MemberRespDTO;

public interface MemberService {

  int registerMember(MemberReqDTO memberReqDTO);

  MemberRespDTO login(LoginDTO loginDTO);

}
