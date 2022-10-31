package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class JsonUtils {
  static String getString(ReadableMap data, String key) {
    return data.hasKey(key) ? data.getString(key) : null;
  }

  static String getString(ReadableMap data, String key, String defaultValue) {
    return data.hasKey(key) ? data.getString(key) : defaultValue;
  }

  static int getInt(ReadableMap data, String key, int defaultValue) {
    return data.hasKey(key) ? data.getInt(key) : defaultValue;
  }

  static double getDouble(ReadableMap data, String key, double defaultValue) {
    return data.hasKey(key) ? data.getDouble(key) : defaultValue;
  }
}
