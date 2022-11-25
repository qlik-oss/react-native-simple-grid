package com.qliktrialreactnativestraighttable;

import android.view.GestureDetector;

public interface Content {
  void updateBackgroundColor(boolean shouldAnimate);
  void setGestureDetector(GestureDetector gestureDetector);
  void setSelected(boolean selected);
  void toggleSelected();
  boolean isSelected();
  void setCellData(DataCell cell, DataRow row, DataColumn column);
  DataCell getCell();
  String getCopyMenuString();
  void copyToClipBoard();
}
