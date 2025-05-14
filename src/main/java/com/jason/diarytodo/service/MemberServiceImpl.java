package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.MemberRespDTO;
import com.jason.diarytodo.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
  private final MemberMapper memberMapper;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public void register(MemberRespDTO member) {
    String encryptedPwd = bCryptPasswordEncoder.encode(member.getPassword());
    member.setPassword(encryptedPwd);

    memberMapper.insertMemberByMember(member);
  }

  @Override
  public MemberRespDTO login(LoginDTO loginDTO) {
    MemberRespDTO member = memberMapper.findMemberById(loginDTO.getLoginId());
    if (member != null && bCryptPasswordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
      // log.info("member: {}", member);
      return member;
    }
    return null;
  }
}
