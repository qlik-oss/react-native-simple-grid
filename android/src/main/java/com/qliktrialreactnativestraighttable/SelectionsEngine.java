package com.qliktrialreactnativestraighttable;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelectionsEngine {

  Set<SelectionsObserver> observers = new HashSet<>();
  Map<String, String> selections = new HashMap<>();

  public void observe(SelectionsObserver observer) {
    observers.add(observer);
  }

  public void remove(SelectionsObserver observer) {
    observers.remove(observer);
  }

  public void selectionsChanged(CustomHorizontalScrollView contextView, String s) {
    String key = getKeyFrom(s);
    if(selections.containsKey(key)) {
      selections.remove(key);
    } else {
      String[] components = SelectionsEngine.getComponents(s);
      selections.put(components[0], components[1]);
    }
    for(SelectionsObserver observer : observers ) {
      observer.onSelectionsChanged(s);
    }
    WritableMap args = Arguments.createMap();
    WritableArray array = Arguments.createArray();
    for (Map.Entry<String, String> entry : selections.entrySet()) {
      String selectionsKey = entry.getKey();
      String selectionsValue = entry.getValue();
      String selectionsString = selectionsKey + selectionsValue;
      array.pushString(selectionsString);
    }
    args.putArray("selections", array);
    FrameLayout parent = (FrameLayout) contextView.getParent();
    EventUtils.sendEventToJSFromView(parent, "onSelectionsChanged", args);
  }

  public void clearSelections() {
    selections.clear();
    for(SelectionsObserver observer : observers ) {
      observer.onClear();
    }
  }

  public boolean contains(DataCell cell) {
    String key = getKeyFrom(cell);
    return selections.containsKey(key);
  }

  static String getSignatureFrom(DataCell cell) {
    return String.format("/%d/%d/%d", cell.qElemNumber, cell.colIdx, cell.rowIdx);
  }

  static String getKeyFrom(String string) {
    int index = string.lastIndexOf("/");
    return string.substring(0, index);
  }

  static String getKeyFrom(DataCell cell) {
    return String.format("/%d/%d", cell.qElemNumber, cell.colIdx);
  }

  static String[] getComponents(String s) {
    String[] items = s.split("/");
    String key = "/" + items[1] + "/" + items[2];
    String val = "/" + items[3];
    String result [] = {key, val};
    return result;
  }
}
