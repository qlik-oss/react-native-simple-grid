package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class DataSize {
  public int qcx = 0;
  public int qcy = 0;
  public DataSize(ReadableMap readableMap) {
    qcx = readableMap.getInt("qcx");
    qcy = readableMap.getInt("qcy");
  }
}
