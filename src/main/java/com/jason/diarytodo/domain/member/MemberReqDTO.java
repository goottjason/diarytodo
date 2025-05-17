package com.jason.diarytodo.domain.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberReqDTO {

  @NotBlank(message = "아이디는 필수입니다.")
  @Size(min = 4, max = 20, message = "아이디는 4~20자로 작성해주세요.")
  private String loginId;                         // 로그인 아이디 (UK)

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 4, max = 20, message = "비밀번호는 4~20자로 작성해주세요.")
  private String password;                        // 비밀번호(암호화 저장)

  @Size(min = 2, max = 20, message = "닉네임은 2~20자로 작성해주세요.")
  private String nickname;                        // 닉네임(선택)

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식으로 작성해주세요.")
  private String email;                           // 이메일 (UK)

  @Builder.Default
  private String profileImage = "avatar.png";     // 프로필 이미지 파일명 또는 URL (기본값, avatar.png)
  @Builder.Default
  private String gender = "U";                    // 성별 ('M', 'F', 'U')
}