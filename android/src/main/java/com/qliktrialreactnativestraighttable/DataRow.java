package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataRow {
  public List<DataCell> cells = new ArrayList<>();
  public DataRow(ReadableMap source, List<DataColumn> columns) {
    ReadableMapKeySetIterator iterator = source.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      if(!key.equalsIgnoreCase("key")) {
        ReadableMap cellItem = source.getMap(key);
        int colIdx = cellItem.getInt("colIdx");
        cells.add(new DataCell(cellItem, columns.get(colIdx)));
      }
    }
    Collections.sort(cells, (a, b) -> a.rawColIdx - b.rawColIdx);
  }

  public String toEvent() throws JSONException {
    JSONObject data = new JSONObject();
    JSONArray cellJson = new JSONArray();
    cells.forEach((DataCell cell) -> {
      try {
        cellJson.put(cell.toEvent());
      } catch (JSONException e) {
        e.printStackTrace();
      }
    });
    data.put("cells", cellJson);
    return data.toString();
  }
}
