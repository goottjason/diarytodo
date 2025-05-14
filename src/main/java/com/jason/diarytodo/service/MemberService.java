package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.Member;

public interface MemberService {
  void register(Member member);

  Member login(LoginDTO loginDTO);
}
