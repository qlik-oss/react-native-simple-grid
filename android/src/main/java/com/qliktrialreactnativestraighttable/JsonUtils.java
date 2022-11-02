package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class JsonUtils {
  static String getString(ReadableMap data, String key) {
    return data.hasKey(key) ? data.getString(key) : null;
  }

  static int getInt(ReadableMap data, String key, int defaultValue) {
    return data.hasKey(key) ? data.getInt(key) : defaultValue;
  }

  static boolean getBoolean(ReadableMap data, String key, boolean defaultValue) {
    return data.hasKey(key) ? data.getBoolean(key) : defaultValue;
  }
}
