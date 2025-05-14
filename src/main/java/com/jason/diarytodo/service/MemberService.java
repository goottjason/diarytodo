package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberRespDTO;

public interface MemberService {

  void register(MemberRespDTO member);

  MemberRespDTO login(LoginDTO loginDTO);

}
