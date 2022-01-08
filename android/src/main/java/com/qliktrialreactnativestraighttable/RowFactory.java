package com.qliktrialreactnativestraighttable;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.util.ArrayList;
import java.util.List;

public class RowFactory {
  List<DataRow> rows = new ArrayList<>();
  public RowFactory(ReadableArray source) {
    for(int i = 0; i < source.size(); i++ ) {
      ReadableMap map = source.getMap(i);
      DataRow row = new DataRow(map);
      rows.add(row);
    }
  }

  public List<DataRow> getRows() {
    return rows;
  }
}
