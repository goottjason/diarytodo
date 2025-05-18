package com.jason.diarytodo.service.member;

import com.jason.diarytodo.domain.member.LoginReqDTO;
import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
  private final MemberMapper memberMapper;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public int registerMember(MemberReqDTO memberReqDTO) {
    String encryptedPwd = bCryptPasswordEncoder.encode(memberReqDTO.getPassword());
    memberReqDTO.setPassword(encryptedPwd);

    return memberMapper.insertMember(memberReqDTO);
  }

  @Override
  public MemberRespDTO checkIdDuplication(String loginId) {
    return memberMapper.selectMemberByLoginId(loginId);
  }

  @Override
  public MemberRespDTO checkEmailUnique(String email) {
    return memberMapper.selectMemberByEmail(email);
  }

  @Override
  public void clearAuthLoginInfo(String loginId) {
    memberMapper.updateAuthLoginInfoToNull(loginId);
  }

  @Override
  public void updateAutoLoginInfo(String loginId, String autoLoginSessionId, LocalDateTime autoLoginLimit) {
    memberMapper.updateAuthLoginInfo(loginId, autoLoginSessionId, autoLoginLimit);
  }

  @Override
  public MemberRespDTO getMemberByAutoLoginSessionId(String autoLoginSessionId) {
    return memberMapper.selectMemberByAutoLoginSessionId(autoLoginSessionId);
  }

  @Override
  public MemberRespDTO login(LoginReqDTO loginDTO) {
    MemberRespDTO member = memberMapper.selectMemberByLoginId(loginDTO.getLoginId());
    if (member != null && bCryptPasswordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
      return member;
    }
    return null;
  }
}
