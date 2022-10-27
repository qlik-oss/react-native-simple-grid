package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DataColumn {

  public Boolean isDim = false;
  public int width = 200;
  public String label;
  public String id;
  public String align;
  public String sortDirection;
  public Representation representation;
  public List<Object> stylingInfo;
  public int dataColIdx = 0;
  public boolean active = false;

  public DataColumn(ReadableMap source) {
    ReadableMap representationMap = source.getMap("representation");
    representation = new Representation(representationMap);
    stylingInfo = source.getArray("stylingInfo").toArrayList();

    isDim = source.getBoolean("isDim");
    width = source.getInt("width");
    label = source.getString("label");
    id = source.getString("id");
    align = source.getString("align");
    sortDirection = source.getString("sortDirection");
    dataColIdx = source.getInt("dataColIdx");
    width = (int)PixelUtils.dpToPx(width);
    if (source.hasKey("active")) {
      active = source.getBoolean("active");
    }
  }

  public String toEvent() throws JSONException {
    JSONObject column = new JSONObject();
    column.put("isDim", isDim);
    column.put("width", width);
    column.put("label", label);
    column.put("id", id);
    column.put("align", align);
    column.put("sortDirection", sortDirection);
    column.put("dataColIdx", dataColIdx);
    column.put("active", active);
    return column.toString();
  }
}
