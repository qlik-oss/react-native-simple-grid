package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class TotalsCell {
  String qText = "";
  TotalsCell(ReadableMap map) {
    qText = map.getString("qText");
  }
}
