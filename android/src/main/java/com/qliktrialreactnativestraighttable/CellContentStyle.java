package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class CellContentStyle extends HeaderContentStyle {
  int rowHeight = 1;
  CellContentStyle(ReadableMap data) {
    super(data);
    rowHeight = JsonUtils.getInt(data, "rowHeight", 1);
  }
}
