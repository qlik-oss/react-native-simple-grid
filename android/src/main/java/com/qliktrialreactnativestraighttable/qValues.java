package com.qliktrialreactnativestraighttable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class qValues {
  List<qValue> values;

  public qValues(List<qValue> values) {
    this.values = values;
  }

  public qValue get(int index){
    return values.get(index);
  }

  public JSONArray toEvent() throws JSONException {
    JSONArray qValues = new JSONArray();

    for(qValue val : values) {
      qValues.put(val.toEvent());
    }

    return qValues;
  }
}
