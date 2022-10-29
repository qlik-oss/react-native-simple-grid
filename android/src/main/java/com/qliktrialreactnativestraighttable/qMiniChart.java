package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class qMiniChart {
  public double qMin;
  public double qMax;
  public qMatrix matrix;
  public qMiniChart(ReadableMap data) {
    qMin = data.hasKey("qMin") ? data.getDouble("qMin") : 0.0;
    qMax = data.hasKey("qMax") ? data.getDouble("qMax") : 0.0;
    ReadableArray dataArray = data.hasKey("qMatrix") ? data.getArray("qMatrix") : null;
    if(dataArray != null) {
      matrix = new qMatrix(dataArray);
    }
  }
}
