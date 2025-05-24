package com.jason.diarytodo.domain.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private Integer errorCode;
  private LocalDateTime timestamp;

  // 데이터 있는 성공
  public static <T> MyResponse<T> success(T data) {
    return MyResponse.<T>builder()
      .success(true)
      .message("요청이 성공적으로 처리되었습니다.")
      .data(data)
      .timestamp(LocalDateTime.now())
      .build();
  }

  // 데이터 없는 성공
  public static <T> MyResponse<T> success() {
    return MyResponse.<T>builder()
      .success(true)
      .message("요청이 성공적으로 처리되었습니다.")
      .data(null)
      .timestamp(LocalDateTime.now())
      .build();
  }

  // 1) 에러 코드만
  public static <T> MyResponse<T> fail(Integer errorCode) {
    return MyResponse.<T>builder()
      .success(false)
      .message("요청이 실패하였습니다.")
      .errorCode(errorCode)
      .timestamp(LocalDateTime.now())
      .build();
  }

  // 2) 메시지만
  public static <T> MyResponse<T> failWithMessage(String message) {
    return MyResponse.<T>builder()
      .success(false)
      .message(message)
      .errorCode(null)
      .timestamp(LocalDateTime.now())
      .build();
  }

  // 3) 메시지와 에러코드 모두
  public static <T> MyResponse<T> fail(Integer errorCode, String message) {
    return MyResponse.<T>builder()
      .success(false)
      .message(message)
      .errorCode(errorCode)
      .timestamp(LocalDateTime.now())
      .build();
  }
}
