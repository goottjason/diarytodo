package com.jason.diarytodo.domain;

import lombok.*;

@Data // getter/setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
  private String loginId;
  private String password;
}