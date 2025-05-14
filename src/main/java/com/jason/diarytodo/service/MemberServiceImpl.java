package com.jason.diarytodo.service;

import com.jason.diarytodo.domain.LoginDTO;
import com.jason.diarytodo.domain.Member;
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
  public void register(Member member) {
    String encryptedPwd = bCryptPasswordEncoder.encode(member.getMemberPwd());
    member.setMemberPwd(encryptedPwd);

    memberMapper.insertMemberByMember(member);
  }

  @Override
  public Member login(LoginDTO loginDTO) {
    Member member = memberMapper.findMemberById(loginDTO.getMemberId());
    if (member != null && bCryptPasswordEncoder.matches(loginDTO.getMemberPwd(), member.getMemberPwd())) {
      // log.info("member: {}", member);
      return member;
    }
    return null;
  }
}
