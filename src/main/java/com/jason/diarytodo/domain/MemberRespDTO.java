package com.jason.diarytodo.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data // getter/setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRespDTO {

  private int memberId;                           // 회원 PK (AI)
  private String loginId;                         // 로그인 아이디 (UK)
  private String password;                        // 비밀번호(암호화 저장)
  private String nickname;                        // 닉네임(선택)
  private String email;                           // 이메일 (UK)
  private LocalDateTime createdAt;                // 가입일시
  @Builder.Default
  private String profileImage = "avatar.png";     // 프로필 이미지 파일명 또는 URL (기본값, avatar.png)
  @Builder.Default
  private int point = 100;                        // 포인트 점수 (기본값, 100)
  @Builder.Default
  private String gender = "U";                    // 성별 ('M', 'F', 'U')
}