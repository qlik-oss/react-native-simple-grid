package com.qliktrialreactnativestraighttable;

import android.view.GestureDetector;
import android.view.MotionEvent;

public interface Content {
  void updateBackgroundColor(boolean shouldAnimate);
  boolean handleTouch(MotionEvent e);
  void setSelected(boolean selected);
  void toggleSelected();
  boolean isSelected();
  void setCellData(DataCell cell, DataRow row, DataColumn column);
  DataCell getCell();
  String getCopyMenuString();
  void copyToClipBoard();
}
