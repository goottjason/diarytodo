package com.jason.diarytodo.domain.member;

import lombok.*;

@Data // getter/setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginReqDTO {
  private String loginId;
  private String password;
}