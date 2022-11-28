package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class qMiniChart {
  public double qMin;
  public double qMax;
  public qMatrix matrix;
  public qMiniChart(ReadableMap data) {
    qMin = data.hasKey("qMin") ? data.getDouble("qMin") : 0.0;
    qMax = data.hasKey("qMax") ? data.getDouble("qMax") : 0.0;
    ReadableArray dataArray = data.hasKey("qMatrix") ? data.getArray("qMatrix") : null;
    if(dataArray != null) {
      matrix = new qMatrix(dataArray);
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
