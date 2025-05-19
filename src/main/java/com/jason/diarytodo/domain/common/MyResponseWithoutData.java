package com.jason.diarytodo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyResponseWithoutData {
  private int code;
  private Object data;
  private String msg;
}
