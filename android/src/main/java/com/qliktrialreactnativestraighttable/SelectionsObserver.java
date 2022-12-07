package com.qliktrialreactnativestraighttable;

public interface SelectionsObserver {
  void onSelectionsChanged(String s);
  void onClear();
  void onRecycled();
}
