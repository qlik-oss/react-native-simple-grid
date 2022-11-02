package com.qliktrialreactnativestraighttable;

import android.view.GestureDetector;

public interface Content {
  void updateBackgroundColor(boolean shouldAnimate);
  void setGestureDetector(GestureDetector gestureDetector);
  void setSelected(boolean selected);
  void toggleSelected();
  boolean isSelected();
  void setCell(DataCell cell);
  DataCell getCell();
  String getCopyMenuString();
  void copyToClipBoard();
}
