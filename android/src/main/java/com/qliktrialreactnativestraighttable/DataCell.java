package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class DataCell {
  String qText;
  Double  qNum;
  int  qElemNumber;
  String  qState;
  int  rowIdx;
  int  colIdx;
  boolean  isDim;
  int rawRowIdx;
  int  rawColIdx;
  public DataCell(ReadableMap source) {
    qText = source.getString("qText");
//    qNum = source.getDouble("qNum");
    qElemNumber =  source.getInt("qElemNumber");
    qState =  source.getString("qState");
    rowIdx =  source.getInt("rowIdx");
    colIdx =  source.getInt("colIdx");
    isDim =  source.getBoolean("isDim");
    rawRowIdx =  source.getInt("rawRowIdx");
    rawColIdx =  source.getInt("rawColIdx");
  }

}
