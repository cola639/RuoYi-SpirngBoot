package com.colaclub.tldraw.enums;

public enum TldrawStatus {
  PRIVATE("0", "私有"),
  PUBLIC("1", "公开");

  private final String code;
  private final String info;

  TldrawStatus(String code, String info) {
    this.code = code;
    this.info = info;
  }

  public String getCode() {
    return code;
  }

  public String getInfo() {
    return info;
  }
}
