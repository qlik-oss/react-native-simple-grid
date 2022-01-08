package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

public class DataColumn {

  public Boolean isDim = false;
  public int width = 200;
  public String label;
  public String id;
  public String align;
  public String sortDirection;
  public int dataColIdx = 0;

  public DataColumn(ReadableMap source) {
    isDim = source.getBoolean("isDim");
    width = source.getInt("width");
    label = source.getString("label");
    id = source.getString("id");
    align = source.getString("align");
    sortDirection = source.getString("sortDirection");
    dataColIdx = source.getInt("dataColIdx");
  }
}
