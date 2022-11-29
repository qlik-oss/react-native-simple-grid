package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class qValue {
  String qNum;
  String qText;

  public qValue(HashMap<String, Object> source) {
    qNum = "" + source.get("qNum");
    qText = "" + source.get("qText");
  }

  public JSONObject toEvent() throws JSONException {
    JSONObject qValue = new JSONObject();
    if(qText != null) {
      qValue.put("qText", qText);
    }
    if(qNum != null) {
      qValue.put("qNum", qNum);
    }

    return qValue;
  }
}
