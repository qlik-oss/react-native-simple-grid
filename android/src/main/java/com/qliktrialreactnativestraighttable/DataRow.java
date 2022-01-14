package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataRow {
  public List<DataCell> cells = new ArrayList<>();
  public DataRow(ReadableMap source) {
    ReadableMapKeySetIterator iterator = source.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      if(!key.equalsIgnoreCase("key")) {
        ReadableMap cellItem = source.getMap(key);
        cells.add(new DataCell(cellItem));
      }
    }
    Collections.sort(cells, (a, b) -> a.rawColIdx - b.rawColIdx);
  }
}
