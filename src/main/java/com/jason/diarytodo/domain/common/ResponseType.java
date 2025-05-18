package com.jason.diarytodo.domain.common;

public enum ResponseType {
  SUCCESS(200), FAIL(400);

  private int resultCode;

  ResponseType(int resultCode) {
    this.resultCode = resultCode;
  }

  public int getResultCode() {
    return resultCode;
  }
  public String getResultMessage() {
    return this.name();
  }
}
