package com.qliktrialreactnativestraighttable;

import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import org.json.JSONException;
import org.json.JSONObject;

public class qMiniChart {
  public double qMin = 0.0;
  public double qMax = 0.0;
  public qMatrix matrix = null;
  
  private double getValue(String key, ReadableMap data) {
    if(data != null) {
      if(data.hasKey(key)){
        if(data.getType(key) == ReadableType.Number) {
          return data.getDouble(key); 
        }
      }
    }
    return 0.0;
  }
  
  public qMiniChart(ReadableMap data) {
    try {
      qMin = getValue("qMin", data);
      qMax = getValue("qMax", data);
      ReadableArray dataArray = data.hasKey("qMatrix") ? data.getArray("qMatrix") : null;
      if (dataArray != null) {
        matrix = new qMatrix(dataArray);
      }
    } catch(Exception e) {
      Log.d("qMiniChart", "Invalid");
    }
  }

  public JSONObject toEvent() throws JSONException {

    JSONObject json = new JSONObject();
    json.put("qMin", qMin);
    json.put("qMax", qMax);
    if(matrix != null) {
      String foo = matrix.toEvent().toString();
      json.put("qMatrix", matrix.toEvent());
    }
    return json;
  }
}
