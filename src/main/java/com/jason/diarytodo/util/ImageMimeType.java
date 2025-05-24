package com.jason.diarytodo.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageMimeType {
  private static final Map<String, String> IMAGE_MIME_TYPE_MAP;

  static {
    Map<String, String> map = new HashMap<>();
    map.put("jpg", "image/jpeg");
    map.put("jpeg", "image/jpeg");
    map.put("gif", "image/gif");
    map.put("png", "image/png");
    IMAGE_MIME_TYPE_MAP = Collections.unmodifiableMap(map);
  }
  private ImageMimeType() {} // 인스턴스 생성 방지

  public static boolean isImage(String ext) {
    if (ext == null) return false;
    return IMAGE_MIME_TYPE_MAP.containsKey(ext.toLowerCase());
  }
  public static String getMimeType(String ext) {
    if (ext == null) return null;
    return IMAGE_MIME_TYPE_MAP.get(ext.toLowerCase());
  }
}
