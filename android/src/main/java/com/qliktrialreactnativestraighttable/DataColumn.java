package com.qliktrialreactnativestraighttable;

import android.view.Gravity;
import android.view.View;

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
  public int width = 0;
  public String label;
  public String id;
  public String sortDirection;
  public Representation representation;
  public List<Object> stylingInfo;
  public String align;
  public int dataColIdx = 0;
  public boolean active = false;
  public int textAlignment = Gravity.LEFT;
  public int rowHeight = TableTheme.rowHeightFactor;
  public DataColumn(ReadableMap source) {
    ReadableMap representationMap = source.getMap("representation");
    representation = new Representation(representationMap);
    stylingInfo = source.getArray("stylingInfo").toArrayList();
    align = source.getString("align");
    isDim = source.getBoolean("isDim");
    label = source.getString("label");
    id = source.getString("id");
    sortDirection = source.getString("sortDirection");
    dataColIdx = source.getInt("dataColIdx");
    if (source.hasKey("active")) {
      active = source.getBoolean("active");
    }
    setupTextAlign();
  }

  private void setupTextAlign() {
    if( align == null) {
      textAlignment = isDim ? Gravity.LEFT : Gravity.RIGHT;
    } else {
      switch (align) {
        case "left":
          textAlignment = Gravity.LEFT;
          break;
        case "center":
          textAlignment = Gravity.CENTER;
          break;
        case "right":
          textAlignment = Gravity.RIGHT;
          break;
        default:
          break;
      }
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

  public boolean isText() {
    if(representation != null) {
      return representation.type.equals("text") || representation.type.equals("url");
    }
    return true;
  }
}
