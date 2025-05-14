package com.jason.diarytodo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // getter/setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberReqDTO {

  private String loginId;                         // 로그인 아이디 (UK)
  private String password;                        // 비밀번호(암호화 저장)
  private String nickname;                        // 닉네임(선택)
  private String email;                           // 이메일 (UK)
  @Builder.Default
  private String profileImage = "avatar.png";     // 프로필 이미지 파일명 또는 URL (기본값, avatar.png)
  @Builder.Default
  private String gender = "U";                    // 성별 ('M', 'F', 'U')
}