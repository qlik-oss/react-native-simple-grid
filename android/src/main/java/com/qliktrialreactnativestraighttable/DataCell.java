package com.qliktrialreactnativestraighttable;

import android.view.Gravity;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

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
  boolean isNumber;
  int textGravity = Gravity.LEFT;
  public DataCell(ReadableMap source) {
    qText = source.getString("qText");
    qElemNumber =  source.getInt("qElemNumber");
    qState =  source.getString("qState");
    rowIdx =  source.getInt("rowIdx");
    colIdx =  source.getInt("colIdx");
    isDim =  source.getBoolean("isDim");
    rawRowIdx =  source.getInt("rawRowIdx");
    rawColIdx =  source.getInt("rawColIdx");
    ReadableType qNumType = source.getType("qNum");
    isNumber = qNumType == ReadableType.Number;
    if (isNumber) {
      textGravity = Gravity.RIGHT;
    }
  }

}
