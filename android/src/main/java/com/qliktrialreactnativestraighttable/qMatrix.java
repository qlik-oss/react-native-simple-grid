package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class qMatrix {
  public List<qMatrixRow> rows = new ArrayList<>();

  class qMatrixColumn {
    public double qElemNumber;
    public double qNum = 0.0;
    public String qText = null;
    public qMatrixColumn(ReadableMap data) {
      qElemNumber = data.hasKey("qElemNumber") ? data.getDouble("qElemNumber") : 0.0;
      if(data.hasKey("qNum")) {
        ReadableType rt = data.getType("qNum");
        if(rt == ReadableType.Number) {
          qNum = data.getDouble("qNum");
        }
      }
      qText = data.hasKey("qText") ? data.getString("qText") : "";
    }

    JSONObject toEvent() throws JSONException {
      JSONObject object = new JSONObject();
      object.put("qElemenNumber", qElemNumber);
      object.put("qNum", qNum);
      if(qText != null){
        object.put("qText", qText);
      }
      return object;
    }
  }

  class qMatrixRow {
    public List<qMatrixColumn> columns = new ArrayList<>();
    public qMatrixRow(ReadableArray dataArray) {
      for(int i = 0; i < dataArray.size(); i++) {
        columns.add(new qMatrixColumn(dataArray.getMap(i)));
      }
    }
    JSONArray toEvent() throws JSONException {
      JSONArray jsonArray = new JSONArray();
      for(qMatrixColumn column : columns) {
        jsonArray.put(column.toEvent());
      }
      return jsonArray;
    }
  }

  public qMatrix(ReadableArray dataArray) {
    for(int i = 0; i < dataArray.size(); i++) {
      ReadableArray da = dataArray.getArray(i);
      rows.add(new qMatrixRow(da));
    }
  }

  JSONArray toEvent() throws JSONException{
    JSONArray jsonArray = new JSONArray();
    for(qMatrixRow row : rows) {
      jsonArray.put(row.toEvent());
    }
    return jsonArray;
  }

}
