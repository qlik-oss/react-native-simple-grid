package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class CellContentStyle extends HeaderContentStyle {
  int rowHeight = 1;
  int lineCount = 1;
  int themedRowHeight = 1;
  CellContentStyle(ReadableMap data) {
    super(data);
    lineCount = JsonUtils.getInt(data, "rowHeight", 1);

  }
}
